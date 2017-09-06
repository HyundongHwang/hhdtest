package com.hhd2002.hhdtest.GlideTest;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.hhd2002.androidbaselib.HhdUtils;
import com.hhd2002.androidbaselib.Thread.HhdAsyncTask;
import com.hhd2002.hhdtest.GlideTest.models.GlideTestImage;
import com.hhd2002.hhdtest.R;

import java.net.URL;

public class GlideTestDetailActivity
        extends AppCompatActivity {

    private SubsamplingScaleImageView imgObj;
    private GlideTestImage _img;

    public static Intent newIntent(Context context, GlideTestImage img) {
        String imgStr = HhdUtils.getGson().toJson(img);

        //noinspection UnnecessaryLocalVariable
        Intent newIntent = new Intent(context, GlideTestDetailActivity.class)
                .putExtra("DATA", imgStr);
        
        return newIntent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.requestFeature(Window.FEATURE_NO_TITLE);
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.layout_glide_test_detail);
        imgObj = (SubsamplingScaleImageView) findViewById(R.id.img_obj);
        String dataStr = getIntent().getStringExtra("DATA");
        _img = HhdUtils.getGson().fromJson(dataStr, GlideTestImage.class);
        imgObj.setMinScale(0.2f);
        imgObj.setMaxScale(5.0f);
    }

    @Override
    protected void onResume() {
        super.onResume();


        new HhdAsyncTask() {
            public Bitmap _bmp;

            @Override
            protected void doInBackground() {
                try {
                    if (_img.sourceType == GlideTestImage.SourceTypes.Local) {
                        _bmp = BitmapFactory.decodeFile(_img.realUri);
                    } else if (_img.sourceType == GlideTestImage.SourceTypes.Web) {
                        URL url = new URL(_img.realUri);
                        _bmp = BitmapFactory.decodeStream(url.openStream());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected void onPostExecute() {
                super.onPostExecute();
                imgObj.setImage(ImageSource.bitmap(_bmp));
            }
        }.execute();
    }
}
