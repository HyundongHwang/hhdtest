package com.hhd2002.hhdtest.FcmTest;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationBuilderWithBuilderAccessor;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hhd2002.androidbaselib.log.HhdLog;
import com.hhd2002.hhdtest.R;

import java.util.Map;

/**
 * Created by hhd on 2017-06-26.
 */

public class MyFcmService
        extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        HhdLog.d("onMessageReceived !!!");
        String from = remoteMessage.getFrom();
        String to = remoteMessage.getTo();
        String messageId = remoteMessage.getMessageId();
        String messageType = remoteMessage.getMessageType();
        String collapseKey = remoteMessage.getCollapseKey();
        Map<String, String> data = remoteMessage.getData();
        RemoteMessage.Notification notification = remoteMessage.getNotification();

        Context context = this.getApplicationContext();
        Intent intent = new Intent(context, FcmTestActivity.class);
        intent.putExtra("from", from);

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String dataStr = gson.toJson(data);
        intent.putExtra("data", dataStr);

        String notificationStr = gson.toJson(notification);
        intent.putExtra("notification", notificationStr);

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pi = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        Uri defSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.drawable.ic_launcher))
                .setContentTitle("타이틀")
                .setContentText("dataStr : " + dataStr)
                .setAutoCancel(true)
                .setSound(defSoundUri)
                .setLights(000000255, 500, 2000)
                .setContentIntent(pi);

        Notification noti = builder.build();
        NotificationManager notiMgr = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        notiMgr.notify(0, noti);
    }
}
