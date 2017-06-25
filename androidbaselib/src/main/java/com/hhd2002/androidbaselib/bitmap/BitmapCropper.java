package com.hhd2002.androidbaselib.bitmap;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;

import java.io.File;
import java.io.IOException;

public class BitmapCropper {

    private int additionalDegreeToRotate = 0;
    private String filePath;

    public BitmapCropper(String filePath) {
        this.filePath = filePath;
    }

    public BitmapCropper rotate(final int additionalDegreeToRotate) {
        this.additionalDegreeToRotate = (additionalDegreeToRotate % 360);
        return this;
    }

    public Bitmap squareCrop(RectF cropRectF, int targetSize) throws Exception {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD_MR1) {
            return squareCropWithNormalDecoder(cropRectF, targetSize);
        } else {
            try {
                return squareCropWithRegionDecoder(cropRectF, targetSize);
            } catch (IOException ignored) {
                return null;
            }
        }
    }

    private Bitmap squareCropWithNormalDecoder(RectF cropRectF, int targetSize) throws Exception {
        final BitmapDecoder bitmapDecoder = BitmapDecoder.newInstance(new File(filePath));
        final int imageWidth = bitmapDecoder.getWidth();
        final int imageHeight = bitmapDecoder.getHeight();
        final int regionSizeOnFile = (int) (imageWidth * cropRectF.width());

        final int targetWidth = targetSize * imageWidth / regionSizeOnFile;
        final int targetHeight = targetSize * imageHeight / regionSizeOnFile;

        final Bitmap decodedImage = bitmapDecoder
                .inPreferredConfig(Bitmap.Config.RGB_565)
                .desiredSize(BitmapDecoder.DesiredSizeType.BEST_FIT, targetWidth, targetHeight)
                .rotateByExif()
                .rotateMore(additionalDegreeToRotate)
                .incSampleSizeUntilNotOOM()
                .safeDecode();
        if (decodedImage == null) {
            return null;
        }
        final int decodedImageWidth = decodedImage.getWidth();
        final int decodedImageHeight = decodedImage.getHeight();

        Bitmap finalImage = null;
        try {
            final Matrix matrix = new Matrix();

            //- Cropping
            final int x = (int) (decodedImageWidth * cropRectF.left);
            final int y = (int) (decodedImageHeight * cropRectF.top);
            int edgeSizeW = (int) (decodedImageWidth * cropRectF.width());
            // int edgeSizeH = (int) (decodedImageHeight * cropRectF.height());

            if (x + edgeSizeW > decodedImageWidth) {
                edgeSizeW = decodedImageWidth - x;
            }
            if (y + edgeSizeW > decodedImageHeight) {
                edgeSizeW = decodedImageHeight - y;
            }

            finalImage = Bitmap.createBitmap(decodedImage, x, y, edgeSizeW, edgeSizeW, matrix, true);
            return finalImage;
        } finally {
            if (finalImage != null && finalImage != decodedImage) {
                if (decodedImage.isRecycled() == false) {
                    decodedImage.recycle();
                }
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD_MR1)
    private Bitmap squareCropWithRegionDecoder(RectF cropRectF, int targetSize) throws IOException {
        final BitmapRegionDecoder bitmapDecoder = BitmapRegionDecoder.newInstance(filePath, false);
        final Matrix matrix = new Matrix();
        matrix.postRotate(additionalDegreeToRotate);

        final int imageWidth = bitmapDecoder.getWidth();
        final int imageHeight = bitmapDecoder.getHeight();
        final int finalRotation = BitmapUtils.guessRotation(matrix);
        if (finalRotation == 90) {
            cropRectF = new RectF(cropRectF.top, 1 - cropRectF.right, cropRectF.bottom, 1 - cropRectF.left);
        } else if (finalRotation == 270) {
            cropRectF = new RectF(1 - cropRectF.bottom, cropRectF.left, 1 - cropRectF.top, cropRectF.right);
        } else if (finalRotation == 180) {
            cropRectF = new RectF(1 - cropRectF.right, 1 - cropRectF.bottom, 1 - cropRectF.left, 1 - cropRectF.top);
        }
        final int regionSizeOnFile = (int) (imageWidth * cropRectF.width());
        final Rect cropRect = new Rect(
                (int) (cropRectF.left * imageWidth),
                (int) (cropRectF.top * imageHeight),
                (int) (cropRectF.right * imageWidth),
                (int) (cropRectF.bottom * imageHeight));

        //- Bitmap Sampling Option
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        options.inSampleSize = 1;
        while (true) {
            final int nextSampleSize = options.inSampleSize + 1;
            final int nextSampledImageSideLength = regionSizeOnFile / nextSampleSize;
            if (targetSize < nextSampledImageSideLength) {
                options.inSampleSize = nextSampleSize;
                continue;
            }
            break;
        }

        //- Decoding only Crop Region
        final Bitmap decodedImage = bitmapDecoder.decodeRegion(cropRect, options);
        final int decodedImageWidth = decodedImage.getWidth();
        final int decodedImageHeight = decodedImage.getHeight();

        Bitmap finalImage = null;
        try {
            //- Scaling
            if (targetSize < decodedImageWidth) {
                final float ratio = targetSize / (float) decodedImageWidth;
                matrix.postScale(ratio, ratio);
            } else if (targetSize < decodedImageHeight) {
                final float ratio = targetSize / (float) decodedImageHeight;
                matrix.postScale(ratio, ratio);
            }

            finalImage = Bitmap.createBitmap(decodedImage, 0, 0, decodedImageWidth, decodedImageHeight, matrix, true);
            return finalImage;
        } finally {
            if (finalImage != null && finalImage != decodedImage) {
                if (decodedImage.isRecycled() == false) {
                    decodedImage.recycle();
                }
            }
        }
    }
}
