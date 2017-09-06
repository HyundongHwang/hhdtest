package com.hhd2002.hhdtest.AlarmTest;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;

import com.hhd2002.hhdtest.R;

import java.util.Calendar;

/**
 * Created by hhd2002 on 2014. 8. 18..
 */
public class AlarmTestActivity
        extends AppCompatActivity {

    AlarmManager alarmMgr;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_alarm_test);
        alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        findViewById(R.id.btn_one_time).setOnClickListener(v -> onClick_btn_one_time());
        findViewById(R.id.btn_repeat).setOnClickListener(v -> onClick_btn_one_repeat());
        findViewById(R.id.btn_stop).setOnClickListener(v -> onClick_btn_one_stop());
    }

    private void onClick_btn_one_time() {
        Intent intent = new Intent(this, AlarmBR.class);
        PendingIntent pendIntent = PendingIntent.getBroadcast(this, 0, intent, 0);

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        cal.add(Calendar.SECOND, 10);

        alarmMgr.set(AlarmManager.RTC, cal.getTimeInMillis(), pendIntent);
    }

    private void onClick_btn_one_repeat() {
        Intent intent = new Intent(this, AlarmBR.class);
        PendingIntent pendIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
        alarmMgr.setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(), 5000, pendIntent);
    }

    private void onClick_btn_one_stop() {
        Intent intent = new Intent(this, AlarmBR.class);
        PendingIntent pendIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
        alarmMgr.cancel(pendIntent);
    }

}