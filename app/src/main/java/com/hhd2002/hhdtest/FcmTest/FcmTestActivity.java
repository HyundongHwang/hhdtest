package com.hhd2002.hhdtest.FcmTest;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.hhd2002.androidbaselib.HhdSampleUiHelper;
import com.hhd2002.androidbaselib.IHhdSampleActivity;

/**
 * Created by hhd on 2017-06-25.
 */

public class FcmTestActivity
        extends AppCompatActivity
        implements IHhdSampleActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        HhdSampleUiHelper uiHelper = new HhdSampleUiHelper(this);

        uiHelper.addSimpleBtn("테스트", new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public String getSampleDesc() {
        return null;
    }
}
