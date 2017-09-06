package com.hhd2002.androidbaselib;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.MotionEvent;
import android.view.TouchDelegate;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hhd2002.androidbaselib.Log.HhdLog;
import com.hhd2002.androidbaselib.Thread.HhdAsyncTask;

import java.io.IOException;
import java.security.MessageDigest;
import java.util.List;
import java.util.Locale;

/**
 * Created by hhd2002 on 2014. 8. 27..
 */
public class HhdUtils {

    private static final String ASSET_PATH_SEGMENT = "android_asset";
    private static final String ASSET_PREFIX = ContentResolver.SCHEME_FILE + ":///" + ASSET_PATH_SEGMENT + "/";
    private static final int ASSET_PREFIX_LENGTH = ASSET_PREFIX.length();
    private static LocationManager _locationMgr;

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

    public static String getPackageInfoStr(Context context) {
        try {
            String packageName = context.getPackageName();
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(packageName, 0);
            int versionCode = pi.versionCode;
            String versionName = pi.versionName;
            String resStr = String.format("packageName[%s] versionCode[%s] versionName[%s]", packageName, versionCode, versionName);
            return resStr;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String getSignSha1HashBase64(Context context) {
        try {
            String packageName = context.getPackageName();
            PackageManager pm = context.getPackageManager();
            PackageInfo packageInfo = pm.getPackageInfo(packageName, PackageManager.GET_SIGNATURES);

            for (Signature signature : packageInfo.signatures) {
                try {
                    MessageDigest md = MessageDigest.getInstance("SHA");
                    md.update(signature.toByteArray());
                    return Base64.encodeToString(md.digest(), Base64.NO_WRAP);
                } catch (Exception e) {
                    HhdLog.d("Unable to get MessageDigest. signature=" + signature, e);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    // https://stackoverflow.com/questions/1343222/is-there-an-example-of-how-to-use-a-touchdelegate-in-android-to-increase-the-siz
    public static void increaseTouchRegion(View view, int sizeMore) {
        View parent = (View) view.getParent();

        // Post in the parent's message queue to make sure the parent
        // lays out its children before we call getHitRect()
        parent.post(() -> {
            Rect rect = new Rect();
            view.getHitRect(rect);
            rect.left -= sizeMore;
            rect.top -= sizeMore;
            rect.right += sizeMore;
            rect.bottom += sizeMore;
            parent.setTouchDelegate(new TouchDelegate(rect, view));
        });
    }


    public interface ICurrentLocationCallback {
        void onResult(Location location, Address address);
    }

    @SuppressLint("MissingPermission")
    public static void getCurrentLocation(Context context, ICurrentLocationCallback callback) {

        if (_locationMgr == null) {
            _locationMgr = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        }

        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                double longitude = location.getLongitude(); //경도
                double latitude = location.getLatitude();   //위도

                new HhdAsyncTask() {
                    Address _firstAddress = null;

                    @Override
                    protected void doInBackground() {
                        try {
                            Geocoder gCoder = new Geocoder(context, Locale.getDefault());
                            List<Address> addressList = gCoder.getFromLocation(latitude, longitude, 1);
                            _firstAddress = addressList.get(0);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    protected void onPostExecute() {
                        callback.onResult(location, _firstAddress);
                    }
                }.execute();
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };

        _locationMgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 1, locationListener);
        _locationMgr.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 100, 1, locationListener);
    }


    public static void applyButtonEffect(View btn, int pressedColor) {
        btn.setClickable(true);

        btn.setOnTouchListener(new View.OnTouchListener() {

            private boolean _captureOldBg = false;
            private Drawable _newBgDrawable;
            private Drawable _oldBgDrawable;

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN: {

                        if (!_captureOldBg) {
                            _oldBgDrawable = view.getBackground();
                            ColorDrawable clrDrawable = new ColorDrawable(pressedColor);

                            if (_oldBgDrawable == null) {
                                _newBgDrawable = clrDrawable;
                            } else {
                                _newBgDrawable = new LayerDrawable(new Drawable[]{_oldBgDrawable, clrDrawable});
                            }

                            _captureOldBg = true;
                        }

                        view.setBackground(_newBgDrawable);
                        view.invalidate();
                        return true;
                    }
                    case MotionEvent.ACTION_UP: {
                        view.setBackground(_oldBgDrawable);
                        view.invalidate();
                        view.callOnClick();
                        return true;
                    }
                    case MotionEvent.ACTION_CANCEL: {
                        view.setBackground(_oldBgDrawable);
                        view.invalidate();
                        return true;
                    }
                }

                return false;
            }

        });
    }


    public static void applyButtonEffect(View btn) {
        applyButtonEffect(btn, 0x80000000);
    }


    ////////////////////////////////////////////////////////////////////////////////
    //context.getPackageName() : com.hhd2002.hhdtest
    //
    //context.getApplicationInfo() : {
    //    "className": "com.hhd2002.hhdtest.DiApplication",
    //    "compatibleWidthLimitDp": 0,
    //    "dataDir": "/data/user/0/com.hhd2002.hhdtest",
    //    "descriptionRes": 0,
    //    "enabled": true,
    //    "enabledSetting": 0,
    //    "flags": 952680262,
    //    "fullBackupContent": 0,
    //    "installLocation": -1,
    //    "largestWidthLimitDp": 0,
    //    "nativeLibraryDir": "/data/app/com.hhd2002.hhdtest-2/lib/arm64",
    //    "nativeLibraryRootDir": "/data/app/com.hhd2002.hhdtest-2/lib",
    //    "nativeLibraryRootRequiresIsa": true,
    //    "overrideRes": 0,
    //    "primaryCpuAbi": "arm64-v8a",
    //    "privateFlags": 0,
    //    "processName": "com.hhd2002.hhdtest",
    //    "publicSourceDir": "/data/app/com.hhd2002.hhdtest-2/base.apk",
    //    "requiresSmallestWidthDp": 0,
    //    "scanPublicSourceDir": "/data/app/com.hhd2002.hhdtest-2",
    //    "scanSourceDir": "/data/app/com.hhd2002.hhdtest-2",
    //    "seinfo": "default",
    //    "sourceDir": "/data/app/com.hhd2002.hhdtest-2/base.apk",
    //    "splitPublicSourceDirs": [
    //        "/data/app/com.hhd2002.hhdtest-2/split_lib_dependencies_apk.apk",
    //        "/data/app/com.hhd2002.hhdtest-2/split_lib_slice_0_apk.apk",
    //        "/data/app/com.hhd2002.hhdtest-2/split_lib_slice_1_apk.apk",
    //        "/data/app/com.hhd2002.hhdtest-2/split_lib_slice_2_apk.apk",
    //        "/data/app/com.hhd2002.hhdtest-2/split_lib_slice_3_apk.apk",
    //        "/data/app/com.hhd2002.hhdtest-2/split_lib_slice_4_apk.apk",
    //        "/data/app/com.hhd2002.hhdtest-2/split_lib_slice_5_apk.apk",
    //        "/data/app/com.hhd2002.hhdtest-2/split_lib_slice_6_apk.apk",
    //        "/data/app/com.hhd2002.hhdtest-2/split_lib_slice_7_apk.apk",
    //        "/data/app/com.hhd2002.hhdtest-2/split_lib_slice_8_apk.apk",
    //        "/data/app/com.hhd2002.hhdtest-2/split_lib_slice_9_apk.apk"
    //        ],
    //    "splitSourceDirs": [
    //        "/data/app/com.hhd2002.hhdtest-2/split_lib_dependencies_apk.apk",
    //        "/data/app/com.hhd2002.hhdtest-2/split_lib_slice_0_apk.apk",
    //        "/data/app/com.hhd2002.hhdtest-2/split_lib_slice_1_apk.apk",
    //        "/data/app/com.hhd2002.hhdtest-2/split_lib_slice_2_apk.apk",
    //        "/data/app/com.hhd2002.hhdtest-2/split_lib_slice_3_apk.apk",
    //        "/data/app/com.hhd2002.hhdtest-2/split_lib_slice_4_apk.apk",
    //        "/data/app/com.hhd2002.hhdtest-2/split_lib_slice_5_apk.apk",
    //        "/data/app/com.hhd2002.hhdtest-2/split_lib_slice_6_apk.apk",
    //        "/data/app/com.hhd2002.hhdtest-2/split_lib_slice_7_apk.apk",
    //        "/data/app/com.hhd2002.hhdtest-2/split_lib_slice_8_apk.apk",
    //        "/data/app/com.hhd2002.hhdtest-2/split_lib_slice_9_apk.apk"
    //    ],
    //    "targetSdkVersion": 26,
    //    "taskAffinity": "com.hhd2002.hhdtest",
    //    "theme": 2131755013,
    //    "uiOptions": 0,
    //    "uid": 10258,
    //    "versionCode": 1,
    //    "banner": 0,
    //    "icon": 2131165406,
    //    "labelRes": 2131689504,
    //    "logo": 0,
    //    "packageName": "com.hhd2002.hhdtest",
    //    "showUserIcon": -10000
    //}
    //
    //context.getResources().getDisplayMetrics() : {
    //    "density": 4.0,
    //    "densityDpi": 640,
    //    "heightPixels": 2392,
    //    "noncompatDensity": 4.0,
    //    "noncompatDensityDpi": 640,
    //    "noncompatHeightPixels": 2392,
    //    "noncompatScaledDensity": 4.0,
    //    "noncompatWidthPixels": 1440,
    //    "noncompatXdpi": 537.882,
    //    "noncompatYdpi": 537.388,
    //    "scaledDensity": 4.0,
    //    "widthPixels": 1440,
    //    "xdpi": 537.882,
    //    "ydpi": 537.388
    //}
    //
    //context.getResources().getConfiguration() : {
    //    "colorNavigationBar": -16777216,
    //    "compatScreenHeightDp": 510,
    //    "compatScreenWidthDp": 320,
    //    "compatSmallestScreenWidthDp": 320,
    //    "densityDpi": 640,
    //    "fontScale": 1.0,
    //    "fontTypeIndex": 0,
    //    "forceEmbolden": 0,
    //    "hardKeyboardHidden": 2,
    //    "keyboard": 1,
    //    "keyboardHidden": 1,
    //    "locale": "ko_KR",
    //    "mcc": 450,
    //    "mnc": 8,
    //    "navigation": 1,
    //    "navigationHidden": 2,
    //    "orientation": 1,
    //    "screenHeightDp": 574,
    //    "screenLayout": 268435794,
    //    "screenWidthDp": 360,
    //    "seq": 7,
    //    "smallestScreenWidthDp": 360,
    //    "themePackage": "com.lge.launcher2.theme.optimus",
    //    "touchscreen": 3,
    //    "uiMode": 17,
    //    "userSetLocale": false
    //}
    //
    //context.getPackageManager().getSystemAvailableFeatures() : [
    //{
    //    "flags": 0,
    //        "name": "android.hardware.sensor.proximity",
    //        "reqGlEsVersion": 0
    //},
    //{
    //    "flags": 0,
    //        "name": "android.hardware.sensor.accelerometer",
    //        "reqGlEsVersion": 0
    //},
    //{
    //    "flags": 0,
    //        "name": "android.hardware.faketouch",
    //        "reqGlEsVersion": 0
    //},
    //{
    //    "flags": 0,
    //        "name": "android.hardware.usb.accessory",
    //        "reqGlEsVersion": 0
    //},
    //{
    //    "flags": 0,
    //        "name": "android.software.backup",
    //        "reqGlEsVersion": 0
    //},
    //{
    //    "flags": 0,
    //        "name": "android.hardware.touchscreen",
    //        "reqGlEsVersion": 0
    //},
    //{
    //    "flags": 0,
    //        "name": "android.hardware.touchscreen.multitouch",
    //        "reqGlEsVersion": 0
    //},
    //{
    //    "flags": 0,
    //        "name": "android.software.print",
    //        "reqGlEsVersion": 0
    //},
    //{
    //    "flags": 0,
    //        "name": "com.lge.software.cliptray",
    //        "reqGlEsVersion": 0
    //},
    //{
    //    "flags": 0,
    //        "name": "android.software.voice_recognizers",
    //        "reqGlEsVersion": 0
    //},
    //{
    //    "flags": 0,
    //        "name": "android.hardware.sensor.gyroscope",
    //        "reqGlEsVersion": 0
    //},
    //{
    //    "flags": 0,
    //        "name": "android.hardware.opengles.aep",
    //        "reqGlEsVersion": 0
    //},
    //{
    //    "flags": 0,
    //        "name": "android.hardware.bluetooth",
    //        "reqGlEsVersion": 0
    //},
    //{
    //    "flags": 0,
    //        "name": "android.hardware.camera.autofocus",
    //        "reqGlEsVersion": 0
    //},
    //{
    //    "flags": 0,
    //        "name": "android.hardware.telephony.gsm",
    //        "reqGlEsVersion": 0
    //},
    //{
    //    "flags": 0,
    //        "name": "android.hardware.usb.host",
    //        "reqGlEsVersion": 0
    //},
    //{
    //    "flags": 0,
    //        "name": "android.hardware.audio.output",
    //        "reqGlEsVersion": 0
    //},
    //{
    //    "flags": 0,
    //        "name": "android.hardware.camera.flash",
    //        "reqGlEsVersion": 0
    //},
    //{
    //    "flags": 0,
    //        "name": "android.hardware.camera.front",
    //        "reqGlEsVersion": 0
    //},
    //{
    //    "flags": 0,
    //        "name": "android.hardware.screen.portrait",
    //        "reqGlEsVersion": 0
    //},
    //{
    //    "flags": 0,
    //        "name": "android.hardware.nfc",
    //        "reqGlEsVersion": 0
    //},
    //{
    //    "flags": 0,
    //        "name": "android.hardware.sensor.stepdetector",
    //        "reqGlEsVersion": 0
    //},
    //{
    //    "flags": 0,
    //        "name": "android.software.home_screen",
    //        "reqGlEsVersion": 0
    //},
    //{
    //    "flags": 0,
    //        "name": "android.hardware.microphone",
    //        "reqGlEsVersion": 0
    //},
    //{
    //    "flags": 0,
    //        "name": "android.hardware.bluetooth_le",
    //        "reqGlEsVersion": 0
    //},
    //{
    //    "flags": 0,
    //        "name": "android.hardware.sensor.compass",
    //        "reqGlEsVersion": 0
    //}
    //
    //context.getPackageManager().getSystemSharedLibraryNames() : [
    //    "com.qualcomm.qcnvitems",
    //    "com.google.widevine.software.drm",
    //    "com.qti.dpmapi",
    //    "com.google.android.media.effects",
    //    "com.lge.broadcast.tdmb",
    //    "com.qti.location.sdk",
    //    "com.lge.resources",
    //    "com.quicinc.wbcservice",
    //    "com.lge.locksettings",
    //    "com.android.location.provider",
    //    "com.lge.systemui",
    //    "com.qualcomm.qti.QtiTelephonyServicelibrary",
    //    "com.quicinc.cneapiclient",
    //    "com.android.future.usb.accessory",
    //    "com.qti.dpmframework",
    //    "javax.obex",
    //    "com.google.android.gms",
    //    "com.android.lge.lgsvcitems",
    //    "com.lge.zdi.splitwindow",
    //    "com.qualcomm.qcrilhook",
    //    "android.test.runner",
    //    "com.lge.mdm",
    //    "com.lge.sui",
    //    "com.google.android.maps",
    //    "ConnectivityExt",
    //    "com.lge.lghiddenlibs",
    //    "org.apache.http.legacy",
    //    "com.broadcom.bt",
    //    "com.android.media.remotedisplay",
    //    "com.quicinc.wbc",
    //    "com.android.mediadrm.signer",
    //    "com.qti.snapdragon.sdk.display"
    //]
    ////////////////////////////////////////////////////////////////////////////////
    public static void dumpContext(Context context) {

        HhdLog.d("context.getPackageName() : " + context.getPackageName());
        HhdLog.d("context.getApplicationInfo() : " + HhdUtils.getGson().toJson(context.getApplicationInfo()));
        HhdLog.d("context.getResources().getDisplayMetrics() : " + HhdUtils.getGson().toJson(context.getResources().getDisplayMetrics()));
        HhdLog.d("context.getResources().getConfiguration() : " + HhdUtils.getGson().toJson(context.getResources().getConfiguration()));
        HhdLog.d("context.getPackageManager().getSystemAvailableFeatures() : " + HhdUtils.getGson().toJson(context.getPackageManager().getSystemAvailableFeatures()));
        HhdLog.d("context.getPackageManager().getSystemSharedLibraryNames() : " + HhdUtils.getGson().toJson(context.getPackageManager().getSystemSharedLibraryNames()));
    }


    public static String dumpBundle(Bundle extras) {
        StringBuilder sb = new StringBuilder();

        for (String key : extras.keySet()) {
            Object value = extras.get(key);
            sb.append("" + key + " : " + value + "\n");
        }

        return sb.toString();
    }


    public static void setButtonAction(View parentView, int btnId, int pressedColor, View.OnClickListener onClick) {
        View btn = parentView.findViewById(btnId);
        HhdUtils.applyButtonEffect(btn, pressedColor);
        btn.setOnClickListener(onClick);
    }


    public static void setButtonAction(Activity activity, int btnId, int pressedColor, View.OnClickListener onClick) {
        View btn = activity.findViewById(btnId);
        HhdUtils.applyButtonEffect(btn, pressedColor);
        btn.setOnClickListener(onClick);
    }

    public static void showToast(Context context, String format, Object... args) {
        String msg = String.format(format, args);
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }
}
