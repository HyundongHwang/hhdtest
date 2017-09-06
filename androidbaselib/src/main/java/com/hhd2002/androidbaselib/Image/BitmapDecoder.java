package com.hhd2002.androidbaselib.Image;

import android.content.ContentResolver;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.net.Uri;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public abstract class BitmapDecoder {

    public static final int DECODE_LOOPING_LIMIT = 10;

    public enum DesiredSizeType {
        BEST_FIT,
        SHORT_SIDE_FIT,
        SHOULD_NOT_EXCEED
    }

    private Matrix transformMatrix = null;
    private ExifReader exifReader = null;
    private final BitmapFactory.Options options = new BitmapFactory.Options();
    private Point size;
    private String mimeType;
    private DesiredSizeType desiredSizeType;
    private Point desiredSize;
    private boolean incSampleSizeUntilNotOOM = false;
    private boolean forViewing = true;

    public static BitmapDecoder newInstance(byte[] data) throws Exception {
        return newInstance(data, 0, data.length);
    }

    public static BitmapDecoder newInstance(byte[] data, int offset, int length) throws Exception {
        if ((offset | length) < 0 || data.length < offset + length) {
            throw new ArrayIndexOutOfBoundsException();
        }
        final BitmapDecoder decoder = new BitmapByteArrayDecoder(data, offset, length);
        decoder.exifReader = new InputStreamExifReader(data, offset, length);
        return decoder;
    }

    public static BitmapDecoder newInstance(FileDescriptor fd) throws Exception {
        return new BitmapFileDescriptorDecoder(fd);
    }

    public static BitmapDecoder newInstance(File file) throws Exception {
        final BitmapDecoder decoder;
        try {
            decoder = new BitmapFileDecoder(file);
        } catch (FileNotFoundException e) {
            throw new Exception(e);
        }

        decoder.exifReader = new FileExifReader(file);
        return decoder;
    }

    public static BitmapDecoder newInstance(Resources res, int id) throws Exception, Resources.NotFoundException {
        return new BitmapResourceDecoder(res, id);
    }

    private static BitmapDecoder newInstance(BitmapStreamDecoder.InputStreamFactory inputStreamFactory) throws Exception {
        final BitmapDecoder decoder;
        decoder = new BitmapStreamDecoder(inputStreamFactory);
        return decoder;
    }

    public static BitmapDecoder newInstance(final AssetManager assetManager, final String fileName) throws Exception {
        return newInstance(new BitmapStreamDecoder.InputStreamFactory() {
            @Override
            public InputStream create() throws IOException {
                return assetManager.open(fileName);
            }
        });
    }

    public static BitmapDecoder newInstance(final ContentResolver resolver, final Uri uri) throws Exception {
        BitmapDecoder decoder = newInstance(new BitmapStreamDecoder.InputStreamFactory() {
            @Override
            public InputStream create() throws IOException {
                return resolver.openInputStream(uri);
            }
        });

        decoder.exifReader = new MediaStoreExifReader(resolver, uri);
        return decoder;
    }

    public Bitmap safeDecode() throws Exception {
        try {
            Bitmap result = decodeInternally();
            if (result == null) {
                throw new Exception("toast_error_for_failed_to_load_image");
            }

            if (DesiredSizeType.SHOULD_NOT_EXCEED == desiredSizeType
                    && (desiredSize != null && desiredSize.x > 0 && (result.getHeight() > desiredSize.y || result.getWidth() > desiredSize.x))) {
                result = scaleToDesiredSize(result);
            }

            return result;
        } finally {
            close();
        }
    }

    private Bitmap scaleToDesiredSize(Bitmap decoded) {
        float scale;

        if (decoded.getHeight() * desiredSize.x > decoded.getWidth() * desiredSize.y) {
            scale = (float) desiredSize.y / decoded.getHeight();
        } else {
            scale = (float) desiredSize.x / decoded.getWidth();
        }

        Matrix scaleM = new Matrix();
        scaleM.setScale(scale, scale);
        Bitmap scaled = BitmapUtils.transform(decoded, scaleM);
        decoded.recycle();
        return scaled;
    }

    public Bitmap decode() throws IOException {
        try {
            return decodeInternally();
        } finally {
            close();
        }
    }

    public BitmapDecoder rotateByExif() {
        if (exifReader == null) {
            throw new UnsupportedOperationException();
        }
        transformMatrix = exifReader.getMatrix();
        return this;
    }

    public BitmapDecoder rotateMore(int additionalDegreeToRotate) {
        if (transformMatrix == null) {
            transformMatrix = new Matrix();
        }
        transformMatrix.postRotate(additionalDegreeToRotate);
        return this;
    }

    public BitmapDecoder desiredSize(DesiredSizeType desiredSizeType, int width, int height) {
        this.desiredSizeType = desiredSizeType;
        this.desiredSize = new Point(width, height);
        return this;
    }

    public BitmapDecoder forViewing(boolean value) {
        this.forViewing = value;
        return this;
    }

    public BitmapDecoder inPreferredConfig(Bitmap.Config decodeConfig) {
        getOptions().inPreferredConfig = decodeConfig;
        return this;
    }

    public BitmapDecoder incSampleSizeUntilNotOOM() {
        this.incSampleSizeUntilNotOOM = true;
        return this;
    }

    public int getWidth() {
        return getSize().x;
    }

    public int getHeight() {
        return getSize().y;
    }

    public BitmapFactory.Options getOptions() {
        return options;
    }

    protected BitmapDecoder() {
    }

    protected abstract Bitmap decode(BitmapFactory.Options opts) throws IOException;

    protected void close() {
    }

    private Bitmap decodeInternally() throws IOException {
        calcSampleSize();
        if (incSampleSizeUntilNotOOM == false) {
            Bitmap decoded = decode(options);
            if (transformMatrix != null) {
                decoded = BitmapUtils.transform(decoded, transformMatrix);
            }
            return BitmapUtils.applyGlMaxTextureSize(decoded);
        } else {
            return decodeAndRotateBitmapUntilNotOOM();
        }
    }

    private void calcSampleSize() {
        if (desiredSizeType != null) {
            switch (desiredSizeType) {
                case SHOULD_NOT_EXCEED:
                case BEST_FIT:
                    options.inSampleSize = getSampleSizeBestFit();
                    break;
                case SHORT_SIDE_FIT:
                    options.inSampleSize = getSampleSizeShortSideFit();
                    break;
            }
            if (options.inSampleSize < 1) {
                options.inSampleSize = 1;
            }
        } else {
            options.inSampleSize = 1;
        }
    }

    private int getSampleSizeBestFit() {
        int sampleSize = 1;
        final int imageWidth = getWidth();
        final int imageHeight = getHeight();

        // Requested Size
        int reqWidth = desiredSize.x;
        int reqHeight = desiredSize.y;
        if (forViewing) {
            reqWidth = BitmapUtils.guaranteeNotExceedMaxBitmapSize(reqWidth);
            reqHeight = BitmapUtils.guaranteeNotExceedMaxBitmapSize(reqHeight);
        }

        if (imageHeight > reqHeight || imageWidth > reqWidth) {
            if (imageWidth > imageHeight) {
                sampleSize = Math.round((float) imageHeight / (float) reqHeight);
            } else {
                sampleSize = Math.round((float) imageWidth / (float) reqWidth);
            }

            // This offers some additional logic in case the image has a strange
            // aspect ratio. For example, a panorama may have a much larger
            // imageWidth than imageHeight. In these cases the total pixels might still
            // end up being too large to fit comfortably in memory, so we should
            // be more aggressive with sample down the image (=larger
            // sampleSize).

            final float totalPixels = imageWidth * imageHeight;

            // Anything more than 2x the requested pixels we'll sample down
            // further.
            final float totalReqPixelsCap = reqWidth * reqHeight * 2;

            while (totalPixels / (sampleSize * sampleSize) > totalReqPixelsCap) {
                sampleSize++;
            }
        }
        if (forViewing) {
            sampleSize = guaranteeNotExceedMaxBitmapSize(imageWidth, imageHeight, sampleSize);
        }
        return sampleSize;
    }

    private int getSampleSizeShortSideFit() {
        final int imageWidth = getWidth();
        final int imageHeight = getHeight();
        int sampleSize = 1;
        if (imageWidth < imageHeight) {
            if (desiredSize.x != 0) {
                sampleSize = Math.round(imageWidth / (float) desiredSize.x);
            }
        } else {
            if (desiredSize.y != 0) {
                sampleSize = Math.round(imageHeight / (float) desiredSize.y);
            }
        }
        if (forViewing) {
            sampleSize = guaranteeNotExceedMaxBitmapSize(imageWidth, imageHeight, sampleSize);
        }
        return sampleSize;
    }

    private int guaranteeNotExceedMaxBitmapSize(int imageWidth, int imageHeight, int sampleSize) {
        if (sampleSize < 1) {
            sampleSize = 1;
        }
        final int sampledWidth = imageWidth / sampleSize;
        final int sampledHeight = imageHeight / sampleSize;
        final int reqWidth = BitmapUtils.guaranteeNotExceedMaxBitmapSize(sampledWidth);
        final int reqHeight = BitmapUtils.guaranteeNotExceedMaxBitmapSize(sampledHeight);
        if (reqWidth < sampledWidth || reqHeight < sampledHeight) {
            if (imageWidth < imageHeight) {
                if (reqHeight != 0) {
                    sampleSize = imageHeight / reqHeight + 1;
                }
            } else {
                if (reqWidth != 0) {
                    sampleSize = imageWidth / reqWidth + 1;
                }
            }
        }
        return sampleSize;
    }

    private Bitmap decodeAndRotateBitmapUntilNotOOM() throws IOException {
        for (int i = 0; i < DECODE_LOOPING_LIMIT; ++i) {
            try {
                Bitmap decoded = decode(options);
                if (transformMatrix != null) {
                    decoded = BitmapUtils.transform(decoded, transformMatrix);
                }
                return BitmapUtils.applyGlMaxTextureSize(decoded);
            } catch (OutOfMemoryError e) {
                increaseSampleSize();
            }
        }
        return null;
    }

    private void increaseSampleSize() {
        options.inSampleSize += 1;
    }

    private boolean isNeededXYSwapping() {
        if (transformMatrix == null) {
            return false;
        }
        return BitmapUtils.isSwappedXY(transformMatrix);
    }

    private Point getImageBounds() {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        try {
            decode(opts);
        } catch (IOException e) {
            return new Point(-1, -1);
        }
        mimeType = opts.outMimeType;
        return new Point(opts.outWidth, opts.outHeight);
    }

    private Point getSize() {
        if (size == null) {
            size = getImageBounds();
            if (isNeededXYSwapping()) {
                final int temp = size.x;
                size.x = size.y;
                size.y = temp;
            }
        }
        return size;
    }

}
