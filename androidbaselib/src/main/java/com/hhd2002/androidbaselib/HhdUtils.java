package com.hhd2002.androidbaselib;

import android.content.ContentResolver;
import android.net.Uri;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created by hhd2002 on 2014. 8. 27..
 */
public class HhdUtils {

    private static final String ASSET_PATH_SEGMENT = "android_asset";
    private static final String ASSET_PREFIX = ContentResolver.SCHEME_FILE + ":///" + ASSET_PATH_SEGMENT + "/";
    private static final int ASSET_PREFIX_LENGTH = ASSET_PREFIX.length();

    public static boolean isAssetUri(Uri uri) {
        return ContentResolver.SCHEME_FILE.equals(uri.getScheme()) && !uri.getPathSegments().isEmpty()
                && ASSET_PATH_SEGMENT.equals(uri.getPathSegments().get(0));
    }

    public static String toAssetPath(Uri uri) {
        return uri.toString().substring(ASSET_PREFIX_LENGTH);
    }

    public static Uri assetPathToUri(String path) {
        final String assetPath = path.startsWith("/") ? path.substring(1) : path;
        return Uri.parse(ASSET_PREFIX + path);
    }

    public static Gson getGson() {
        //noinspection UnnecessaryLocalVariable
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson;
    }
}