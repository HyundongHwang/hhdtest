package com.hhd2002.androidbaselib;

import android.content.ContentUris;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.provider.MediaStore;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by hhd on 2017-07-07.
 */

public class HhdSPrefModel {
    public String key;
    public SharedPreferences pref;
    public SharedPreferences.Editor editor;
    public Timer timer;
    public Object cachedValue;

    public HhdSPrefModel(Context context, String key) {
        this.key = key;
        pref = context.getSharedPreferences(key, Context.MODE_PRIVATE);
        editor = pref.edit();
        timer = new Timer();
    }

    public void putString(String value) {
        if (value.equals(cachedValue))
            return;

        editor.putString(key, value);
        cachedValue = value;
        commitDelayed();
    }

    public void putInt(int value) {
        if (cachedValue != null && value == (int) cachedValue)
            return;

        editor.putInt(key, value);
        cachedValue = value;
        commitDelayed();
    }

    public void putBoolean(boolean value) {
        if (cachedValue != null && value == (boolean) cachedValue)
            return;

        editor.putBoolean(key, value);
        cachedValue = value;
        commitDelayed();
    }

    public String getString() {
        if (!HhdStringUtils.isStringNullOrEmpty((String) cachedValue))
            return (String) cachedValue;

        cachedValue = pref.getString(key, "");
        return (String) cachedValue;
    }

    public int getInt() {
        if (cachedValue != null)
            return (int) cachedValue;

        cachedValue = pref.getInt(key, 0);
        return (int) cachedValue;
    }

    public boolean getBoolen() {
        if (cachedValue != null)
            return (boolean) cachedValue;

        cachedValue = pref.getBoolean(key, false);
        return (boolean) cachedValue;
    }

    public void remove() {
        editor.remove(key);
        cachedValue = null;
        commitDelayed();
    }

    private void commitDelayed() {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                editor.commit();
            }
        }, 500);
    }

    public static Uri toImageMediaStoreUri(long id) {
        return ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
    }

    public static Uri toVideoMediaStoreUri(long id) {
        return ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id);
    }
}

