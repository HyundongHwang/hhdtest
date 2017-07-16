package com.hhd2002.hhdtest.WifiManTest;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Formatter;

import com.hhd2002.androidbaselib.HhdSampleUiHelper;

/**
 * Created by hhd on 2017-07-12.
 */

public class WifiManTestActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WifiManager wm = (WifiManager)this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo connectionInfo = wm.getConnectionInfo();
        String ip = Formatter.formatIpAddress(connectionInfo.getIpAddress());

        HhdSampleUiHelper uiHelper = new HhdSampleUiHelper(this);
        uiHelper.writeLog(String.format("ip : %s", ip));
    }
}
