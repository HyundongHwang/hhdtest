package com.hhd2002.hhdtest.GlideTest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.hhd2002.androidbaselib.IHhdSampleActivity;
import com.hhd2002.hhdtest.GlideTest.util.SystemUiHider;
import com.hhd2002.hhdtest.R;

import org.parceler.Parcels;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class GlideTestDetailActivity
        extends AppCompatActivity
        implements IHhdSampleActivity {

    private static final String KEY_ITEM = "KEY_ITEM";
    private SubsamplingScaleImageView imgObj;
    private GlideTestActivity.Item item;

    public static Intent newIntent(Context context, GlideTestActivity.Item item) {
        return new Intent(context, GlideTestDetailActivity.class)
                .putExtra(KEY_ITEM, Parcels.wrap(item));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Window window = getWindow();
        window.requestFeature(Window.FEATURE_NO_TITLE);
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.layout_glide_test_detail);

        imgObj = (SubsamplingScaleImageView) findViewById(R.id.img_obj);
        item = Parcels.unwrap(getIntent().getParcelableExtra(KEY_ITEM));
        imgObj.setMinScale(0.2f);
        imgObj.setMaxScale(5.0f);
    }

    @Override
    protected void onResume() {
        super.onResume();
        imgObj.setImage(ImageSource.uri(item.mediaStoreRealUri));
    }

    @Override
    public String getSampleDesc() {
        return null;
    }
}
