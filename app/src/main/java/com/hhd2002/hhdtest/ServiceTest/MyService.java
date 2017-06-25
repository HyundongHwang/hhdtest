package com.hhd2002.hhdtest.ServiceTest;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.AsyncQueryHandler;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.hhd2002.androidbaselib.log.HhdLog;

/**
 * Created by hhd on 2017-06-23.
 */

public class MyService extends Service {

    @Override
    public void onCreate() {
        HhdLog.d("onCreate");
        super.onCreate();
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        HhdLog.d("onStartCommand");

        if (intent == null)
            return Service.START_STICKY;



        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {



                for (int i = 0; i < 10; i++) {
                    String msg = intent.getStringExtra("msg");
                    HhdLog.d("onStartCommand msg : %s", msg);

                    for (int j = 0; j < 5; j++) {
                        try {
                            Thread.sleep(1000);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        HhdLog.d("onStartCommand waiting ... : %d", j);
                    }


                    Intent newIntent = new Intent(MyService.this.getApplicationContext(), MyServiceActivity.class);

                    newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                            Intent.FLAG_ACTIVITY_SINGLE_TOP |
                            Intent.FLAG_ACTIVITY_CLEAR_TOP);

                    newIntent.putExtra("msg", "SERVICE -> ACTIVITY");
                    MyService.this.startActivity(newIntent);
                }



                return null;
            }
        }.execute();



        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
