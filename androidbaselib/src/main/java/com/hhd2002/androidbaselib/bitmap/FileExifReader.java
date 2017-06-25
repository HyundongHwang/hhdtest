package com.hhd2002.androidbaselib.bitmap;

import android.graphics.Matrix;
import android.media.ExifInterface;
import android.util.Log;

import java.io.File;
import java.util.WeakHashMap;

public class FileExifReader extends JpegExifReader {
    private final int orientation;

    private static WeakHashMap<String,Integer> cache = new WeakHashMap<String, Integer>();

    public FileExifReader(File file) {
        String absPath = file.getAbsolutePath();

        if( cache.containsKey(absPath)) {
            orientation = cache.get(absPath);
        } else {
            int orientationVal = 0;
            try {
                ExifInterface exif = new ExifInterface(absPath);
                orientationVal = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            } catch (Exception e) {
                Log.i("hhddebug", "FileExifReader.FileExifReader e : " + e);
            }
            orientation = orientationVal;
        }
    }

    @Override
    public Matrix getMatrix() {
        try {
            return getMatrixFromJpegOrientation(orientation);
        } catch (Exception e) {
            Log.i("hhddebug", "FileExifReader.getMatrix e : " + e);
        }
        return new Matrix();
    }

    public int getOrientation() {
        return orientation;
    }
}
