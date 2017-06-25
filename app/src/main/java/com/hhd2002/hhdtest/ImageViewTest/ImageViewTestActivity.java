package com.hhd2002.hhdtest.ImageViewTest;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.hhd2002.androidbaselib.IHhdSampleActivity;
import com.hhd2002.hhdtest.R;

public class ImageViewTestActivity
		extends AppCompatActivity
		implements IHhdSampleActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_image_view_test);
	}

    @Override
    public String getSampleDesc() {
        return "ImageView 샘플";
    }
}