package com.hhd2002.androidbaselib.bitmap;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.FileDescriptor;

/*package*/ class BitmapFileDescriptorDecoder extends BitmapDecoder {
    private final FileDescriptor fd;

    public BitmapFileDescriptorDecoder(FileDescriptor fd) {
        this.fd = fd;
    }

    @Override
    protected Bitmap decode(BitmapFactory.Options opts) {
        return BitmapFactory.decodeFileDescriptor(fd, null, opts);
    }

    @Override
    protected void close() {
    }
}
