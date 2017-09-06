package com.hhd2002.hhdtest.HhdLogTest;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.hhd2002.androidbaselib.Log.HhdLog;
import com.hhd2002.androidbaselib.SampleUI.HhdSampleUiHelper;

public class HhdLogTestActivity
        extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        HhdSampleUiHelper uiHelper = new HhdSampleUiHelper(this);

        uiHelper.addSimpleBtn("write log to azure hello world", (View v) -> {
            HhdLog.d("hello world");
        });
    }
}
