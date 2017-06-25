package com.hhd2002.hhdtest.AlarmTest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by hhd2002 on 2014. 8. 18..
 */
public class AlarmBR extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "AlarmBR onReceive", Toast.LENGTH_LONG).show();
    }
}
