package com.hhd2002.androidbaselib.Image;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.graphics.Matrix;
import android.os.Build;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

// from android-19: com/android/ex/photo/util/Exif.java
public class InputStreamExifReader extends JpegExifReader {
    private static final String TAG = "CameraExif";

    private final InputStream inputStream;
    private final long length;

    public InputStreamExifReader(byte[] data, int offset, int length) {
        this.inputStream = new ByteArrayInputStream(data, offset, length);
        this.length = length;
    }

    @Override
    public Matrix getMatrix() {
        return getMatrixFromJpegOrientation(getOrientation(inputStream, length));
    }

    /**
     * Returns the degrees in clockwise. Values are 0, 90, 180, or 270.
     *
     * @param inputStream The input stream will not be closed for you.
     * @param byteSize    Recommended parameter declaring the length of the input stream. If you
     *                    pass in -1, we will have to read more from the input stream.
     * @return 0-8
     */
    public static int getOrientation(final InputStream inputStream, final long byteSize) {
        if (inputStream == null) {
            return 0;
        }

        /*
          Looking at this algorithm, we never look ahead more than 8 bytes. As long as we call
          advanceTo() at the end of every loop, we should never have to reallocate a larger buffer.

          Also, the most we ever read backwards is 4 bytes. pack() reads backwards if the encoding
          is in little endian format. These following two lines potentially reads 4 bytes backwards:

          int tag = pack(jpeg, offset, 4, false);
          count = pack(jpeg, offset - 2, 2, littleEndian);

          To be safe, we will always advance to some index-4, so we'll need 4 more for the +8
          look ahead, which makes it a +12 look ahead total. Use 16 just in case my analysis is off.

          This means we only need to allocate a single 16 byte buffer.

          Note: If you do not pass in byteSize parameter, a single large allocation will occur.
          For a 1MB image, I see one 30KB allocation. This is due to the line containing:

          has(jpeg, byteSize, offset + length - 1)

          where length is a variable int (around 30KB above) read from the EXIF headers.

          This is still much better than allocating a 1MB byte[] which we were doing before.
         */

        final int lookAhead = 16;
        final int readBackwards = 4;
        final InputStreamBuffer jpeg = new InputStreamBuffer(inputStream, lookAhead, false);

        int offset = 0;
        int length = 0;

        if (has(jpeg, byteSize, 1)) {
            // JPEG image files begin with FF D8. Only JPEG images have EXIF data.
            final boolean possibleJpegFormat = jpeg.get(0) == (byte) 0xFF
                    && jpeg.get(1) == (byte) 0xD8;
            if (!possibleJpegFormat) {
                return 0;
            }
        }

        // ISO/IEC 10918-1:1993(E)
        while (has(jpeg, byteSize, offset + 3) && (jpeg.get(offset++) & 0xFF) == 0xFF) {
            final int marker = jpeg.get(offset) & 0xFF;

            // Check if the marker is a padding.
            if (marker == 0xFF) {
                continue;
            }
            offset++;

            // Check if the marker is SOI or TEM.
            if (marker == 0xD8 || marker == 0x01) {
                continue;
            }
            // Check if the marker is EOI or SOS.
            if (marker == 0xD9 || marker == 0xDA) {
                // Loop ends.
                jpeg.advanceTo(offset - readBackwards);
                break;
            }

            // Get the length and check if it is reasonable.
            length = pack(jpeg, offset, 2, false);
            if (length < 2 || !has(jpeg, byteSize, offset + length - 1)) {
                Log.i("hhddebug", "InputStreamExifReader.getOrientation Invalid length : ");
                return 0;
            }

            // Break if the marker is EXIF in APP1.
            if (marker == 0xE1 && length >= 8 &&
                    pack(jpeg, offset + 2, 4, false) == 0x45786966 &&
                    pack(jpeg, offset + 6, 2, false) == 0) {
                offset += 8;
                length -= 8;
                // Loop ends.
                jpeg.advanceTo(offset - readBackwards);
                break;
            }

            // Skip other markers.
            offset += length;
            length = 0;

            // Loop ends.
            jpeg.advanceTo(offset - readBackwards);
        }

        // JEITA CP-3451 Exif Version 2.2
        if (length > 8) {
            // Identify the byte order.
            int tag = pack(jpeg, offset, 4, false);
            if (tag != 0x49492A00 && tag != 0x4D4D002A) {
                Log.i("hhddebug", "InputStreamExifReader.getOrientation Invalid byte order");
                return 0;
            }
            final boolean littleEndian = (tag == 0x49492A00);

            // Get the offset and check if it is reasonable.
            int count = pack(jpeg, offset + 4, 4, littleEndian) + 2;
            if (count < 10 || count > length) {
                Log.i("hhddebug", "InputStreamExifReader.getOrientation Invalid offset");
                return 0;
            }
            offset += count;
            length -= count;

            // Offset has changed significantly.
            jpeg.advanceTo(offset - readBackwards);

            // Get the count and go through all the elements.
            count = pack(jpeg, offset - 2, 2, littleEndian);

            while (count-- > 0 && length >= 12) {
                // Get the tag and check if it is orientation.
                tag = pack(jpeg, offset, 2, littleEndian);
                if (tag == 0x0112) {
                    // We do not really care about type and count, do we?
                    final int orientation = pack(jpeg, offset + 8, 2, littleEndian);
                    switch (orientation) {
                        case 1:
                        case 2:
                        case 3:
                        case 4:
                        case 5:
                        case 6:
                        case 7:
                        case 8:
                            return orientation;
                    }
                    Log.i("hhddebug", "InputStreamExifReader.getOrientation Unsupported orientation");
                    return 0;
                }
                offset += 12;
                length -= 12;

                // Loop ends.
                jpeg.advanceTo(offset - readBackwards);
            }
        }

        return 0;
    }

    private static int pack(final InputStreamBuffer bytes, int offset, int length,
                            final boolean littleEndian) {
        int step = 1;
        if (littleEndian) {
            offset += length - 1;
            step = -1;
        }

        int value = 0;
        while (length-- > 0) {
            value = (value << 8) | (bytes.get(offset) & 0xFF);
            offset += step;
        }
        return value;
    }

    private static boolean has(final InputStreamBuffer jpeg, final long byteSize, final int index) {
        if (byteSize >= 0) {
            return index < byteSize;
        } else {
            // For large values of index, this will cause the internal buffer to resize.
            return jpeg.has(index);
        }
    }

    /**
     * Wrapper for {@link InputStream} that allows you to read bytes from it like a byte[]. An
     * internal buffer is kept as small as possible to avoid large unnecessary allocations.
     * <p/>
     * <p/>
     * Care must be taken so that the internal buffer is kept small. The best practice is to
     * precalculate the maximum buffer size that you will need. For example,
     * say you have a loop that reads bytes from index <code>0</code> to <code>10</code>,
     * skips to index <code>N</code>, reads from index <code>N</code> to <code>N+10</code>, etc. Then
     * you know that the internal buffer can have a maximum size of <code>10</code>,
     * and you should set the <code>bufferSize</code> parameter to <code>10</code> in the constructor.
     * <p/>
     * <p/>
     * Use {@link #advanceTo(int)} to declare that you will not need to access lesser indexes. This
     * helps to keep the internal buffer small. In the above example, after reading bytes from index
     * <code>0</code> to <code>10</code>, you should call <code>advanceTo(N)</code> so that internal
     * buffer becomes filled with bytes from index <code>N</code> to <code>N+10</code>.
     * <p/>
     * <p/>
     * If you know that you are reading bytes from a <strong>strictly</strong> increasing or equal
     * index, then you should set the <code>autoAdvance</code> parameter to <code>true</code> in the
     * constructor. For complicated access patterns, or when you prefer to control the internal
     * buffer yourself, set <code>autoAdvance</code> to <code>false</code>. When
     * <code>autoAdvance</code> is enabled, every time an index is beyond the buffer length,
     * the buffer will be shifted forward such that the index requested becomes the first element in
     * the buffer.
     * <p/>
     * <p/>
     * All public methods with parameter <code>index</code> are absolute indexed. The index is from
     * the beginning of the wrapped input stream.
     */
    public static class InputStreamBuffer {

        private static final boolean DEBUG = false;
        private static final int DEBUG_MAX_BUFFER_SIZE = 80;
        private static final String TAG = "InputStreamBuffer";

        private InputStream mInputStream;
        private byte[] mBuffer;
        private boolean mAutoAdvance;
        /**
         * Byte count the buffer is offset by.
         */
        private int mOffset = 0;
        /**
         * Number of bytes filled in the buffer.
         */
        private int mFilled = 0;

        /**
         * Construct a new wrapper for an InputStream.
         * <p/>
         * <p/>
         * If <code>autoAdvance</code> is true, behavior is undefined if you call {@link #get(int)}
         * or {@link #has(int)} with an index N, then some arbitrary time later call {@link #get(int)}
         * or {@link #has(int)} with an index M < N. The wrapper may return the right value,
         * if the buffer happens to still contain index M, but more likely it will throw an
         * {@link IllegalStateException}.
         * <p/>
         * <p/>
         * If <code>autoAdvance</code> is false, you must be diligent and call {@link #advanceTo(int)}
         * at the appropriate times to ensure that the internal buffer is not unnecessarily resized
         * and reallocated.
         *
         * @param inputStream The input stream to wrap. The input stream will not be closed by the
         *                    wrapper.
         * @param bufferSize  The initial size for the internal buffer. The buffer size should be
         *                    carefully chosen to avoid resizing and reallocating the internal buffer.
         *                    The internal buffer size used will be the least power of two greater
         *                    than this parameter.
         * @param autoAdvance Determines the behavior when you need to read an index that is beyond
         *                    the internal buffer size. If true, the internal buffer will shift so
         *                    that the requested index becomes the first element. If false,
         *                    the internal buffer size will grow to the smallest power of 2 which is
         *                    greater than the requested index.
         */
        public InputStreamBuffer(final InputStream inputStream, int bufferSize,
                                 final boolean autoAdvance) {
            mInputStream = inputStream;
            if (bufferSize <= 0) {
                throw new IllegalArgumentException(
                        String.format("Buffer size %d must be positive.", bufferSize));
            }
            bufferSize = leastPowerOf2(bufferSize);
            mBuffer = new byte[bufferSize];
            mAutoAdvance = autoAdvance;
        }

        /**
         * Attempt to get byte at the requested index from the wrapped input stream. If the internal
         * buffer contains the requested index, return immediately. If the index is less than the
         * head of the buffer, or the index is greater or equal to the size of the wrapped input stream,
         * a runtime exception is thrown.
         * <p/>
         * <p/>
         * If the index is not in the internal buffer, but it can be requested from the input stream,
         * {@link #fill(int)} will be called first, and the byte at the index returned.
         * <p/>
         * <p/>
         * You should always call {@link #has(int)} with the same index, unless you are sure that no
         * exceptions will be thrown as described above.
         * <p/>
         * <p/>
         * Consider calling {@link #advanceTo(int)} if you know that you will never request a lesser
         * index in the future.
         *
         * @param index The requested index.
         * @return The byte at that index.
         */
        public byte get(final int index) throws IllegalStateException, IndexOutOfBoundsException {
            Trace.beginSection("get");
            if (has(index)) {
                final int i = index - mOffset;
                Trace.endSection();
                return mBuffer[i];
            } else {
                Trace.endSection();
                throw new IndexOutOfBoundsException(
                        String.format("Index %d beyond length.", index));
            }
        }

        /**
         * Attempt to return whether the requested index is within the size of the wrapped input
         * stream. One side effect is {@link #fill(int)} will be called.
         * <p/>
         * <p/>
         * If this method returns true, it is guaranteed that {@link #get(int)} with the same index
         * will not fail. That means that if the requested index is within the size of the wrapped
         * input stream, but the index is less than the head of the internal buffer,
         * a runtime exception is thrown.
         * <p/>
         * <p/>
         * See {@link #get(int)} for caveats. A lot of the same warnings about exceptions and
         * <code>advanceTo()</code> apply.
         *
         * @param index The requested index.
         * @return True if requested index is within the size of the wrapped input stream. False if
         * the index is beyond the size.
         */
        public boolean has(final int index) throws IllegalStateException, IndexOutOfBoundsException {
            Trace.beginSection("has");
            if (index < mOffset) {
                Trace.endSection();
                throw new IllegalStateException(
                        String.format("Index %d is before buffer %d", index, mOffset));
            }

            final int i = index - mOffset;

            // Requested index not in internal buffer.
            if (i >= mFilled || i >= mBuffer.length) {
                Trace.endSection();
                return fill(index);
            }

            Trace.endSection();
            return true;
        }

        /**
         * Attempts to advance the head of the buffer to the requested index. If the index is less
         * than the head of the buffer, the internal state will not be changed.
         * <p/>
         * <p/>
         * Advancing does not fill the internal buffer. The next {@link #get(int)} or
         * {@link #has(int)} call will fill the buffer.
         */
        public void advanceTo(final int index) throws IllegalStateException, IndexOutOfBoundsException {
            Trace.beginSection("advance to");
            final int i = index - mOffset;
            if (i <= 0) {
                // noop
                Trace.endSection();
                return;
            } else if (i < mFilled) {
                // Shift elements starting at i to position 0.
                shiftToBeginning(i);
                mOffset = index;
                mFilled = mFilled - i;
            } else if (mInputStream != null) {
                // Burn some bytes from the input stream to match the new index.
                int burn = i - mFilled;
                boolean empty = false;
                int fails = 0;
                try {
                    while (burn > 0) {
                        final long burned = mInputStream.skip(burn);
                        if (burned <= 0) {
                            fails++;
                        } else {
                            burn -= burned;
                        }

                        if (fails >= 5) {
                            empty = true;
                            break;
                        }
                    }
                } catch (IOException ignored) {
                    empty = true;
                }

                if (empty) {
                    //Mark input stream as consumed.
                    mInputStream = null;
                }

                mOffset = index - burn;
                mFilled = 0;
            } else {
                // Advancing beyond the input stream.
                mOffset = index;
                mFilled = 0;
            }

            Log.i("hhddebug", "InputStreamBuffer.advanceTo " + String.format("advanceTo %d buffer: %s", i, this));
            Trace.endSection();
        }

        /**
         * Attempt to fill the internal buffer fully. The buffer will be modified such that the
         * requested index will always be in the buffer. If the index is less
         * than the head of the buffer, a runtime exception is thrown.
         * <p/>
         * <p/>
         * If the requested index is already in bounds of the buffer, then the buffer will just be
         * filled.
         * <p/>
         * <p/>
         * Otherwise, if <code>autoAdvance</code> was set to true in the constructor,
         * {@link #advanceTo(int)} will be called with the requested index,
         * and then the buffer filled. If <code>autoAdvance</code> was set to false,
         * we allocate a single larger buffer of a least multiple-of-two size that can contain the
         * requested index. The elements in the old buffer are copied over to the head of the new
         * buffer. Then the entire buffer is filled.
         *
         * @param index The requested index.
         * @return True if the byte at the requested index has been filled. False if the wrapped
         * input stream ends before we reach the index.
         */
        private boolean fill(final int index) {
            Trace.beginSection("fill");
            if (index < mOffset) {
                Trace.endSection();
                throw new IllegalStateException(
                        String.format("Index %d is before buffer %d", index, mOffset));
            }

            int i = index - mOffset;
            // Can't fill buffer anymore if input stream is consumed.
            if (mInputStream == null) {
                Trace.endSection();
                return false;
            }

            // Increase buffer size if necessary.
            int length = i + 1;
            if (length > mBuffer.length) {
                if (mAutoAdvance) {
                    advanceTo(index);
                    i = index - mOffset;
                } else {
                    length = leastPowerOf2(length);

                    String log = String.format(
                            "Increasing buffer length from %d to %d. Bad buffer size chosen, "
                                    + "or advanceTo() not called.",
                            mBuffer.length, length);

                    Log.i("hhddebug", "InputStreamBuffer.fill " + log);
                    mBuffer = Arrays.copyOf(mBuffer, length);
                }
            }

            // Read from input stream to fill buffer.
            int read = -1;
            try {
                read = mInputStream.read(mBuffer, mFilled, mBuffer.length - mFilled);
            } catch (IOException ignored) {
            }

            if (read != -1) {
                mFilled = mFilled + read;
            } else {
                // Mark input stream as consumed.
                mInputStream = null;
            }

            Log.i("hhddebug", "InputStreamBuffer.fill " + String.format("fill %d      buffer: %s", i, this));
            Trace.endSection();
            return i < mFilled;
        }

        /**
         * Modify the internal buffer so that all the bytes are shifted towards the head by
         * <code>i</code>. In other words, the byte at index <code>i</code> will now be at index
         * <code>0</code>. Bytes from a lesser index are tossed.
         *
         * @param i How much to shift left.
         */
        private void shiftToBeginning(final int i) {
            if (i >= mBuffer.length) {
                throw new IndexOutOfBoundsException(
                        String.format("Index %d out of bounds. Length %d", i, mBuffer.length));
            }
            for (int j = 0; j + i < mFilled; j++) {
                mBuffer[j] = mBuffer[j + i];
            }
        }

        @Override
        @SuppressLint("DefaultLocale")
        public String toString() {
            if (DEBUG) {
                return toDebugString();
            }
            return String.format("+%d+%d [%d]", mOffset, mBuffer.length, mFilled);
        }

        public String toDebugString() {
            Trace.beginSection("to debug string");
            final StringBuilder sb = new StringBuilder();
            sb.append("+").append(mOffset);
            sb.append("+").append(mBuffer.length);
            sb.append(" [");
            for (int i = 0; i < mBuffer.length && i < DEBUG_MAX_BUFFER_SIZE; i++) {
                if (i > 0) {
                    sb.append(",");
                }
                if (i < mFilled) {
                    sb.append(String.format("%02X", mBuffer[i]));
                } else {
                    sb.append("__");
                }
            }
            if (mInputStream != null) {
                sb.append("...");
            }
            sb.append("]");

            Trace.endSection();
            return sb.toString();
        }

        /**
         * Calculate the least power of two greater than or equal to the input.
         */
        private static int leastPowerOf2(int n) {
            n--;
            n |= n >> 1;
            n |= n >> 2;
            n |= n >> 4;
            n |= n >> 8;
            n |= n >> 16;
            n++;
            return n;
        }
    }

    /**
     * Stand-in for {@link android.os.Trace}.
     */
    private static class Trace {

        /**
         * Begins systrace tracing for a given tag. No-op on unsupported platform versions.
         *
         * @param tag systrace tag to use
         * @see android.os.Trace#beginSection(String)
         */
        @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
        public static void beginSection(String tag) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                android.os.Trace.beginSection(tag);
            }
        }

        /**
         * Ends systrace tracing for the most recently begun section. No-op on unsupported platform
         * versions.
         *
         * @see android.os.Trace#endSection()
         */
        @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
        public static void endSection() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                android.os.Trace.endSection();
            }
        }

    }


    private static class Arrays {
        /**
         * Copies {@code newLength} elements from {@code original} into a new array.
         * If {@code newLength} is greater than {@code original.length}, the result is padded
         * with the value {@code (byte) 0}.
         *
         * @param original  the original array
         * @param newLength the length of the new array
         * @return the new array
         * @throws NegativeArraySizeException if {@code newLength < 0}
         * @throws NullPointerException       if {@code original == null}
         * @since 1.6
         */
        public static byte[] copyOf(byte[] original, int newLength) {
            if (newLength < 0) {
                throw new NegativeArraySizeException(Integer.toString(newLength));
            }
            return copyOfRange(original, 0, newLength);
        }

        /**
         * Copies elements from {@code original} into a new array, from indexes start (inclusive) to
         * end (exclusive). The original order of elements is preserved.
         * If {@code end} is greater than {@code original.length}, the result is padded
         * with the value {@code (byte) 0}.
         *
         * @param original the original array
         * @param start    the start index, inclusive
         * @param end      the end index, exclusive
         * @return the new array
         * @throws ArrayIndexOutOfBoundsException if {@code start < 0 || start > original.length}
         * @throws IllegalArgumentException       if {@code start > end}
         * @throws NullPointerException           if {@code original == null}
         * @since 1.6
         */
        public static byte[] copyOfRange(byte[] original, int start, int end) {
            if (start > end) {
                throw new IllegalArgumentException();
            }
            int originalLength = original.length;
            if (start < 0 || start > originalLength) {
                throw new ArrayIndexOutOfBoundsException();
            }
            int resultLength = end - start;
            int copyLength = Math.min(resultLength, originalLength - start);
            byte[] result = new byte[resultLength];
            System.arraycopy(original, start, result, 0, copyLength);
            return result;
        }
    }
}
