package com.hhd2002.androidbaselib;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class HhdRuntimePermissionUtils {


    
    private static final int REQUEST_CODE_REQUESTED_PERMISSIONS = 1000;



    //https://developer.android.com/training/permissions/requesting.html?hl=ko
    //https://developer.android.com/guide/topics/security/permissions.html?hl=ko#normal-dangerous
    //http://gun0912.tistory.com/55
    public static String[] getBannedRuntimePermission(Activity activity) {

        try {
            String myPackageName = activity.getPackageName();
            PackageInfo myPi = null;
            Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
            mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
            List<ResolveInfo> riList = activity.getPackageManager().queryIntentActivities(mainIntent, 0);

            for (ResolveInfo ri : riList) {
                try {

                    PackageInfo pi = activity.getPackageManager().getPackageInfo(
                            ri.activityInfo.packageName,
                            PackageManager.GET_PERMISSIONS);

                    if (pi.packageName.contains(myPackageName)) {
                        myPi = pi;
                        break;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            ArrayList<String> banedPermList = new ArrayList<>();

            for (String perm : myPi.requestedPermissions) {

                int permCheckResult = ContextCompat.checkSelfPermission(
                        activity,
                        perm);

                if (permCheckResult != PackageManager.PERMISSION_GRANTED) {
                    banedPermList.add(perm);
                }
            }

            return banedPermList.toArray(new String[banedPermList.size()]);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }



    public static void requestRuntimePermission(
            Activity activity,
            String[] banedPermArray) {

        if (banedPermArray.length == 0)
            return;



        ActivityCompat.requestPermissions(
                activity,
                banedPermArray,
                REQUEST_CODE_REQUESTED_PERMISSIONS);
        // 필요한 권한과 요청 코드를 넣어서 권한허가요청에 대한 결과를 받아야 합니다
    }


    public static void processRuntimePermission(
            Activity activity,
            int requestCode,
            String permissions[],
            int[] grantResults) {

        switch (requestCode) {
            case REQUEST_CODE_REQUESTED_PERMISSIONS: {

                boolean allPermGranted = false;

                for (int i = 0; i < permissions.length; i++) {
                    String perm = permissions[i];
                    int grantResult = grantResults[i];

                    if (grantResult == PackageManager.PERMISSION_GRANTED) {
                        // 권한 허가
                        // 해당 권한을 사용해서 작업을 진행할 수 있습니다
                        allPermGranted = true;
                    } else {
                        // 권한 거부
                        // 사용자가 해당권한을 거부했을때 해주어야 할 동작을 수행합니다
                        allPermGranted = false;
                        break;
                    }
                }

                if (!allPermGranted) {
                    activity.finishAffinity();
                }
            }
        }
    }
}
