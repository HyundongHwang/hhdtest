package com.hhd2002.androidbaselib.bitmap;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;
import java.io.InputStream;

/*package*/ class BitmapStreamDecoder extends BitmapDecoder {
    private final InputStreamFactory inputStreamFactory;
    private InputStream inputStreamToUseAtFirst;

    public BitmapStreamDecoder(InputStreamFactory inputStreamFactory) throws IOException {
        this.inputStreamFactory = inputStreamFactory;
        inputStreamToUseAtFirst = inputStreamFactory.create();
    }

    @Override
    protected Bitmap decode(BitmapFactory.Options opts) throws IOException {
        final InputStream inputStreamToUse;
        if (inputStreamToUseAtFirst != null) {
            inputStreamToUse = inputStreamToUseAtFirst;
            inputStreamToUseAtFirst = null;
        } else {
            inputStreamToUse = inputStreamFactory.create();
        }
        return BitmapFactory.decodeStream(inputStreamToUse, null, opts);
    }

    public interface InputStreamFactory {
        public InputStream create() throws IOException;
    }
}
