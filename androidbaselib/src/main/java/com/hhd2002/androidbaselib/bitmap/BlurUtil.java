package com.hhd2002.androidbaselib.bitmap;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.hhd2002.androidbaselib.stackblur.StackBlurManager;

public class BlurUtil {
    static final int radius = 50;

    public static Bitmap blur(Bitmap bkg) {
        int sizeSplit =
                Math.max(Math.min(bkg.getWidth() / 180, bkg.getHeight() / 180), 1);

        Bitmap copied = Bitmap.createScaledBitmap(bkg, bkg.getWidth() / sizeSplit, bkg.getHeight() / sizeSplit, true);
        try {
            return new StackBlurManager(copied).process(radius);
        } catch (Throwable t) {
            return copied;
        }
    }

    public static void applyBlurAsync(ImageView target, Bitmap bitmap) {
        new BlurAsyncTask(target, bitmap).execute();
    }

    private static class BlurAsyncTask extends AsyncTask<Void, Void, Bitmap> {
        private final Bitmap source;
        private final ImageView targetView;

        BlurAsyncTask(ImageView targetView, Bitmap source) {
            this.source = source;
            this.targetView = targetView;
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            return BlurUtil.blur(source);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            targetView.setImageBitmap(bitmap);
        }
    }
}
