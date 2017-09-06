package com.hhd2002.androidbaselib;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;

import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.List;

public class HhdIntentUtils {
    private static final String INSTALL_SHORTCUT = "com.android.launcher.action.INSTALL_SHORTCUT";
    private static final String EXTRA_DUPLICATE = "duplicate";

    /**
     * Indicates whether the specified action can be used as an intent. This method queries the package manager for installed packages that can respond to an
     * intent with the specified action. If no suitable package is found, this method returns false.
     *
     * @param context The application's environment.
     * @param action  The Intent action to check for availability.
     * @return True if an Intent with the specified action can be sent and responded to, false otherwise.
     * @see <a href="http://android-developers.blogspot.com/2009/01/can-i-use-this-intent.html">http://android-developers.blogspot.com/2009/01/can-i-use-this-intent.html</a>
     */
    public static boolean isIntentAvailable(Context context, String action) {
        return isIntentAvailable(context, new Intent(action));
    }

    public static boolean isIntentAvailable(Context context, Intent intent) {
        List<ResolveInfo> list = context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return (list != null && list.size() > 0);
    }

    public static Intent getIntentForPackageName(Context context, String pakageName) {
        return context.getPackageManager().getLaunchIntentForPackage(pakageName);
    }

    public static Intent getPackageMarketDetailIntent(Context context) {
        return getPackageMarketDetailIntent(context, context.getPackageName());
    }

    public static Intent getPackageMarketDetailIntent(Context context, String packageName) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(String.format("market://details?id=%s", packageName)));
        if (isIntentAvailable(context, intent))
            return intent;

        return new Intent(Intent.ACTION_VIEW, Uri.parse(String.format("https://play.google.com/store/apps/details?id=%s", packageName)));
    }

    public static boolean isInstalledApp(Context context, String packageName) {
        try {
            PackageInfo i = context.getPackageManager().getPackageInfo(packageName, 0);
            @SuppressWarnings("unused")
            String name = i.versionName;
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static boolean isInstalledApp(Context context, String packageName, String version) {
        try {
            PackageInfo i = context.getPackageManager().getPackageInfo(packageName, 0);
            @SuppressWarnings("unused")
            String current = i.versionName;

            try {
                if (TextUtils.isEmpty(current)) {
                    return false;
                }
                int availableVersion = Integer.parseInt(version.replace(".", ""));
                int currentVersion = Integer.parseInt(current.replace(".", ""));

                return availableVersion <= currentVersion;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isInstalledApp(Context context, String packageName, int versionCode) {
        try {
            PackageInfo i = context.getPackageManager().getPackageInfo(packageName, 0);
            int currentVersionCode = i.versionCode;
            return versionCode <= currentVersionCode;
        } catch (Exception e) {
            return false;
        }
    }

    public static Intent getKakaoLinkIntent(String clientId) {
        StringBuilder sb = new StringBuilder("kakaoauth://requestemailidlogin?client_id=");
        sb.append(clientId);
        return new Intent(Intent.ACTION_SEND, Uri.parse(sb.toString()));
    }

    public static Intent getAppMarketIntent(Context context, String pkgName) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + pkgName));
        if (HhdIntentUtils.isIntentAvailable(context, intent)) {
            return intent;
        } else {
            return new Intent(Intent.ACTION_VIEW, Uri.parse(String.format("https://play.google.com/store/apps/details?id=" + pkgName)));
        }
    }

    public static Intent getKakaoTalkFriendPicker(boolean isChatroom) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("kakaotalk://friendspicker?chatRoom=" + isChatroom));
        return intent;
    }

    public static Intent getImageCaptureIntent(Uri output) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, output);
        return intent;
    }

    public static Intent getHomeIntent() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS | Intent.FLAG_ACTIVITY_FORWARD_RESULT | Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);

        return intent;
    }

    public static Intent getAndroidViewIntent(String url) {
        return new Intent(Intent.ACTION_VIEW).setData(Uri.parse(url));
    }

    /**
     * 현재 화면에 떠 있는 activity 클래스명. 패키지 빠진 클래스명이므로 XXX.class.getName()
     * android.permission.GET_TASKS 퍼미션 필요
     *
     * @param context
     * @return
     */
    public static String getCurrentForegroundActivityName(Context context) {
        String pkgName = context.getPackageName();
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> info;
        info = activityManager.getRunningTasks(1);

        for (Iterator<ActivityManager.RunningTaskInfo> iterator = info.iterator(); iterator.hasNext(); ) {
            ActivityManager.RunningTaskInfo runningTaskInfo = iterator.next();
            if (runningTaskInfo.topActivity.getClassName().startsWith(pkgName)) {
                return runningTaskInfo.topActivity.getClassName();
            }
        }
        return null;
    }

    public static Intent buildGeoIntent(double longitude, double latitude, String title) {
        final String latLng = longitude + "," + latitude;
        final String geoUrl = "geo:" + latLng;
        return new Intent(Intent.ACTION_VIEW, Uri.parse(geoUrl));
    }

    public static Intent buildDaumMapIntent(double longitude, double latitude, String title) {
        final String latLng = longitude + "," + latitude;
        final String geoUrl = "daummaps://look?p=" + latLng;
        return new Intent(Intent.ACTION_VIEW, Uri.parse(geoUrl));
    }

    public static Intent buildIntentFromWebViewUrl(Context context, String url) throws URISyntaxException {
        final Intent intent = Intent.parseUri(url, 0);
        if (Intent.ACTION_VIEW.equals(intent.getAction())) {
            intent.addCategory(Intent.CATEGORY_BROWSABLE);
            final String packageName = context.getPackageName();
            intent.putExtra(android.provider.Browser.EXTRA_APPLICATION_ID, packageName);
        }
        return intent;
    }
}
