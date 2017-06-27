package com.hhd2002.hhdtest.HhdLogTest;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.hhd2002.androidbaselib.HhdSampleUiHelper;
import com.hhd2002.androidbaselib.IHhdSampleActivity;
import com.hhd2002.androidbaselib.log.HhdLog;

public class HhdLogTestActivity
        extends AppCompatActivity
        implements IHhdSampleActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        HhdSampleUiHelper uiHelper = new HhdSampleUiHelper(this);

        uiHelper.addSimpleBtn("write log to azure hello world", (View v) -> {
            HhdLog.d("hello world");
        });
    }

    @Override
    public String getSampleDesc() {
        return "HhdLog, Azure, TableService";
    }
}
