package com.hhd2002.androidbaselib.bitmap;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/*package*/ class BitmapByteArrayDecoder extends BitmapDecoder {
    private final byte[] data;
    private final int length;
    private final int offset;

    public BitmapByteArrayDecoder(byte[] data, int offset, int length) {
        this.data = data;
        this.offset = offset;
        this.length = length;
    }

    @Override
    protected Bitmap decode(BitmapFactory.Options opts) {
        return BitmapFactory.decodeByteArray(data, offset, length, opts);
    }
}
