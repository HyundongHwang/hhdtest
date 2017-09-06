package com.hhd2002.androidbaselib.Image;

import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Matrix;
import android.net.Uri;
import android.provider.MediaStore;

public class MediaStoreExifReader implements ExifReader {
    private final ContentResolver resolver;
    private final Uri uri;

    public MediaStoreExifReader(ContentResolver resolver, Uri uri) {
        this.resolver = resolver;
        this.uri = uri;
    }

    @Override
    public Matrix getMatrix() {
        final Matrix matrix = new Matrix();
        final String[] cursorColumns = {MediaStore.Images.Media.ORIENTATION};
        final Cursor cursor = resolver.query(uri, cursorColumns, null, null, null);
        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    matrix.postRotate(cursor.getInt(0));
                }
            } finally {
                try {
                    cursor.close();
                } catch (Throwable t) {
                }
            }
        }
        return matrix;
    }
}