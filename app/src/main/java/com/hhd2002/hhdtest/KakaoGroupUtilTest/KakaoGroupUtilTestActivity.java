package com.hhd2002.hhdtest.KakaoGroupUtilTest;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.hhd2002.androidbaselib.HhdSprefUtils;
import com.hhd2002.androidbaselib.SampleUI.HhdSampleUiHelper;

import java.util.Date;

public class KakaoGroupUtilTestActivity
        extends AppCompatActivity {

    private HhdSampleUiHelper sampleHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sampleHelper = new HhdSampleUiHelper(this);

        sampleHelper.addSimpleBtn("btn1", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sampleHelper.writeLog("btn1 log new Date().toString() : " + new Date().toString());
            }
        });

        sampleHelper.addSimpleBtn("SPref save", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HhdSprefUtils.putSPrefString(KakaoGroupUtilTestActivity.this, "1", "일이삼");
                HhdSprefUtils.putSPrefInt(KakaoGroupUtilTestActivity.this, "2", 2);
                HhdSprefUtils.putSPrefBoolean(KakaoGroupUtilTestActivity.this, "3", true);
            }
        });

        sampleHelper.addSimpleBtn("SPref change", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HhdSprefUtils.putSPrefString(KakaoGroupUtilTestActivity.this, "1", "원투쓰리");
                HhdSprefUtils.putSPrefInt(KakaoGroupUtilTestActivity.this, "2", 200);
                HhdSprefUtils.putSPrefBoolean(KakaoGroupUtilTestActivity.this, "3", false);
            }
        });

        sampleHelper.addSimpleBtn("SPref load", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("hhddebug", "KakaoGroupUtilTestActivity.onClick HhdUtils.getSPrefString(1) : " + HhdSprefUtils.getSPrefString("1"));
                Log.i("hhddebug", "KakaoGroupUtilTestActivity.onClick HhdUtils.getSPrefInt(2) : " + HhdSprefUtils.getSPrefInt("2"));
                Log.i("hhddebug", "KakaoGroupUtilTestActivity.onClick HhdUtils.putSPrefBoolean(3) : " + HhdSprefUtils.getSPrefBoolean("3"));
            }
        });
    }
}
