package com.hhd2002.androidbaselib;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Point;
import android.hardware.Camera;
import android.os.Build;
import android.provider.Settings;
import android.util.TypedValue;
import android.view.Display;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.WindowManager;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;


public class HhdDeviceUtils {

    private static Display display;

    private HhdDeviceUtils() {
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

    public static float dip2Pixel(Context context, float dip) {
        Resources res = context.getResources();
        float pixel = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, res.getDisplayMetrics());
        return pixel;
    }

    @SuppressWarnings("deprecation")
    public static int getDisplayWidth(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            return getDisplayWidthPostHoneycombMr2(context);
        } else {
            return getDisplay(context).getWidth();
        }
    }

    public static Display getDisplay(Context context) {
        if (display == null) {
            synchronized (HhdDeviceUtils.class) {
                if (display != null)
                    return display;
                display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
            }
        }
        return display;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private static int getDisplayWidthPostHoneycombMr2(Context context) {
        Point size = new Point();
        getDisplay(context).getSize(size);
        return size.x;
    }
}
