package com.hhd2002.hhdtest.FcmTest;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.hhd2002.androidbaselib.log.HhdLog;

/**
 * Created by hhd on 2017-06-26.
 */

public class MyFcmInstanceService
        extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        String fcmToken = FirebaseInstanceId.getInstance().getToken();
        HhdLog.d("onTokenRefresh fcmToken : %s", fcmToken);

    }
}
