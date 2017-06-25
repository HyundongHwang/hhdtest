package com.hhd2002.hhdtest.KakaoGroupUtilTest;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.hhd2002.androidbaselib.HhdSampleUiHelper;
import com.hhd2002.androidbaselib.HhdUtil;
import com.hhd2002.androidbaselib.IHhdSampleActivity;

import java.util.Date;

public class KakaoGroupUtilTestActivity
        extends AppCompatActivity
        implements IHhdSampleActivity {

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
                HhdUtil.putSPrefString(KakaoGroupUtilTestActivity.this, "1", "일이삼");
                HhdUtil.putSPrefInt(KakaoGroupUtilTestActivity.this, "2", 2);
                HhdUtil.putSPrefBoolean(KakaoGroupUtilTestActivity.this, "3", true);
            }
        });

        sampleHelper.addSimpleBtn("SPref change", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HhdUtil.putSPrefString(KakaoGroupUtilTestActivity.this, "1", "원투쓰리");
                HhdUtil.putSPrefInt(KakaoGroupUtilTestActivity.this, "2", 200);
                HhdUtil.putSPrefBoolean(KakaoGroupUtilTestActivity.this, "3", false);
            }
        });

        sampleHelper.addSimpleBtn("SPref load", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("hhddebug", "KakaoGroupUtilTestActivity.onClick HhdUtils.getSPrefString(1) : " + HhdUtil.getSPrefString("1"));
                Log.i("hhddebug", "KakaoGroupUtilTestActivity.onClick HhdUtils.getSPrefInt(2) : " + HhdUtil.getSPrefInt("2"));
                Log.i("hhddebug", "KakaoGroupUtilTestActivity.onClick HhdUtils.putSPrefBoolean(3) : " + HhdUtil.getSPrefBoolean("3"));
            }
        });
    }

    @Override
    public String getSampleDesc() {
        return "ScrollView, RecyclerView, LinearLayoutManager, 코드로만 구현, RecyclerView.Adapter";
    }
}
