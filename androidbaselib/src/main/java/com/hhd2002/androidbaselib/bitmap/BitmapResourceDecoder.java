package com.hhd2002.androidbaselib.bitmap;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.TypedValue;

/*package*/ class BitmapResourceDecoder extends BitmapDecoder {
    private final Resources resource;
    private final int resourceId;

    public BitmapResourceDecoder(Resources res, int resourceId) throws Resources.NotFoundException {
        this.resource = res;
        this.resourceId = resourceId;
        if (res.openRawResource(resourceId, new TypedValue()) != null) {
        }
    }

    @Override
    public Bitmap decode(BitmapFactory.Options opts) {
        return BitmapFactory.decodeResource(resource, resourceId, opts);
    }
}
