package com.hhd2002.androidbaselib;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Build;
import android.provider.Settings;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;


public class DeviceUtils {

    private DeviceUtils() {
    }

    @SuppressLint({"NewApi"})
    public static boolean hasCamera(Context context) {
        if (Build.VERSION.SDK_INT >= 9) {
            return Camera.getNumberOfCameras() > 0;
        } else {
            return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
        }
    }

    public static boolean hasNavigationBar() {
        boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
        boolean hasHomeKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_HOME);
        return !hasBackKey || !hasHomeKey;
    }

    public static boolean checkAvailablePlayServiceAndWarnError(Activity activity) {
        if (activity == null) return false;
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity);
        if (status != ConnectionResult.SUCCESS) { // Google Play Services are not available
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, activity, 0x4001);
            dialog.show();
            return false;
        }
        return true;

    }

    public static String getHwId(Context context) {

        if (context == null) {
            return "contextisnull";
        }



        @SuppressLint("HardwareIds")
        String hwid = Settings.Secure.getString(
                context.getContentResolver(),
                Settings.Secure.ANDROID_ID);

        return hwid;
    }
}
