package com.hhd2002.hhdtest;

import android.support.multidex.MultiDexApplication;

import com.hhd2002.androidbaselib.Log.HhdLog;


/**
 * Created by hhd on 2017-06-17.
 */

public class MainApplication extends MultiDexApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        //"DefaultEndpointsProtocol=https;AccountName=hhdandroidtest;AccountKey=zJpcXJ...;EndpointSuffix=core.windows.net";
        HhdLog.init(getBaseContext(), MySecureKeys.AZURE_STORAGE_CONNECTION_STRING);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}