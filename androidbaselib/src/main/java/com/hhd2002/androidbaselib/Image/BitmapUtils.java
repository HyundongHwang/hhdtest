package com.hhd2002.androidbaselib.Image;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

public class BitmapUtils {

    public static int OPEN_GL_MAX_TEXTURE_SIZE = 2048;

    public static Bitmap fromDrawable(Drawable d) {
        if (d == null) {
            return null;
        }
        if (d instanceof BitmapDrawable) {
            return ((BitmapDrawable) d).getBitmap();
        }

        Bitmap bitmap;
        int width = Math.max(d.getIntrinsicWidth(), 1);
        int height = Math.max(d.getIntrinsicHeight(), 1);
        try {
            bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            d.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            d.draw(canvas);
        } catch (Exception e) {
            Log.i("hhddebug", "BitmapUtils.fromDrawable e : " + e);
            bitmap = null;
        }

        return bitmap;
    }

    public static Bitmap fromDrawable(Drawable d, int width, int height) {
        if (d == null) {
            return null;
        }
        if (d instanceof BitmapDrawable) {
            return ((BitmapDrawable) d).getBitmap();
        }

        Bitmap bitmap;
        try {
            bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            d.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            d.draw(canvas);
        } catch (Exception e) {
            Log.i("hhddebug", "BitmapUtils.fromDrawable e : " + e);
            bitmap = null;
        }
        return bitmap;
    }

    private BitmapUtils() {

    }

    /*package*/
    static Bitmap rotate(Bitmap src, int degree) {
        if (src == null) {
            return null;
        } else if (degree == 0) {
            return src;
        }

        final Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        return transform(src, matrix);
    }

    public static Bitmap transform(Bitmap src, Matrix matrix) {
        if (matrix.isIdentity()) {
            return src;
        }

        final Bitmap dst = Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
        if (dst != null && src != dst && src.isRecycled() == false) {
            src.recycle();
        }
        return dst;
    }

    public static int guaranteeNotExceedMaxBitmapSize(int widthOrHeight) {
        final int maxBitmapSize = OPEN_GL_MAX_TEXTURE_SIZE;
        if (maxBitmapSize == 0) {
            return widthOrHeight;
        }
        return Math.min(widthOrHeight, maxBitmapSize);
    }

    public static void setOpenGlMaxTextureSize(int size) {
        if (size != 0) {
            OPEN_GL_MAX_TEXTURE_SIZE = size;
        }
    }

    public static Bitmap applyGlMaxTextureSize(Bitmap src) {
        if (src == null) {
            return null;
        }

        int width = src.getWidth();
        int height = src.getHeight();

        if (OPEN_GL_MAX_TEXTURE_SIZE == 0) {
            return src;
        }

        if (width <= OPEN_GL_MAX_TEXTURE_SIZE && height <= OPEN_GL_MAX_TEXTURE_SIZE) {
            return src;
        }

        float scale;
        if (width > OPEN_GL_MAX_TEXTURE_SIZE) {
            scale = (float) OPEN_GL_MAX_TEXTURE_SIZE / width;
        } else {
            scale = (float) OPEN_GL_MAX_TEXTURE_SIZE / height;
        }

        int scaledWidth = (int) (width * scale);
        int scaledHeight = (int) (height * scale);
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(src, scaledWidth, scaledHeight, true);
        src.recycle();
        return scaledBitmap;
    }

    public static boolean isSwappedXY(Matrix matrix) {
        final float[] values = new float[9];
        matrix.getValues(values);
        return values[Matrix.MSCALE_X] == 0;
    }

    public static int guessRotation(Matrix matrix) {
        final float[] values = new float[9];
        matrix.getValues(values);
        if (values[Matrix.MSCALE_X] == 1.f && values[Matrix.MSCALE_Y] == 1.f) {
            return 0;
        } else if (values[Matrix.MSKEW_X] == -1.f && values[Matrix.MSKEW_Y] == 1.f) {
            return 90;
        } else if (values[Matrix.MSCALE_X] == -1.f && values[Matrix.MSCALE_Y] == -1.f) {
            return 180;
        } else if (values[Matrix.MSKEW_X] == 1.f && values[Matrix.MSKEW_Y] == -1.f) {
            return 270;
        }
        return 0;
    }
}
