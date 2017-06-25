package com.hhd2002.hhdtest;

import android.app.Application;
import android.support.multidex.MultiDexApplication;

import com.hhd2002.androidbaselib.log.HhdLog;

/**
 * Created by hhd on 2017-06-17.
 */

public class MainApplication extends MultiDexApplication {
    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}