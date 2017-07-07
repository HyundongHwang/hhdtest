package com.hhd2002.androidbaselib;

import android.content.Context;

import java.util.HashMap;

public class HhdSprefUtils {
    private static HashMap<String, HhdSPrefModel> sPrefCache = new HashMap<>();
    
    private static HhdSPrefModel getsPrefModel(Context context, String key) {
        HhdSPrefModel model = sPrefCache.get(key);

        if (model == null) {
            model = new HhdSPrefModel(context, key);
            sPrefCache.put(key, model);
        }

        return model;
    }
    
    public static void putSPrefString(Context context, String key, String value) {
        HhdSPrefModel model = getsPrefModel(context, key);
        model.putString(value);
    }

    public static void putSPrefInt(Context context, String key, int value) {
        HhdSPrefModel model = getsPrefModel(context, key);
        model.putInt(value);
    }

    public static void putSPrefBoolean(Context context, String key, boolean value) {
        HhdSPrefModel model = getsPrefModel(context, key);
        model.putBoolean(value);
    }

    public static String getSPrefString(String key) {
        HhdSPrefModel model = sPrefCache.get(key);

        if (model == null)
            return null;

        return (String) model.cachedValue;
    }

    public static int getSPrefInt(String key) {
        HhdSPrefModel model = sPrefCache.get(key);

        if (model == null)
            return 0;

        return (int) model.cachedValue;
    }

    public static boolean getSPrefBoolean(String key) {
        HhdSPrefModel model = sPrefCache.get(key);

        if (model == null)
            return false;

        return (boolean) model.cachedValue;
    }
}
