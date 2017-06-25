package com.hhd2002.androidbaselib.bitmap;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;
import java.io.FileNotFoundException;

/*package*/ class BitmapFileDecoder extends BitmapDecoder {
    private final String pathName;

    public BitmapFileDecoder(File file) throws FileNotFoundException {
        if (file.exists() == false) {
            throw new FileNotFoundException();
        }
        this.pathName = file.getPath();
    }

    @Override
    protected Bitmap decode(BitmapFactory.Options opts) {
        return BitmapFactory.decodeFile(pathName, opts);
    }

    @Override
    protected void close() {
    }
}
