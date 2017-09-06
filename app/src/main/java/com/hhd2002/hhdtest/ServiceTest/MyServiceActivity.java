package com.hhd2002.hhdtest.ServiceTest;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.hhd2002.androidbaselib.SampleUI.HhdSampleUiHelper;


/**
 * Created by hhd on 2017-06-23.
 */

public class MyServiceActivity
        extends AppCompatActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        _processIntent(this.getIntent());
        HhdSampleUiHelper uiHelper = new HhdSampleUiHelper(this);

        uiHelper.addSimpleBtn("서비스로 인텐트 전달", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyServiceActivity.this, MyService.class);
                intent.putExtra("msg", "ACTIVITY -> SERVICE");
                startService(intent);
            }
        });
    }

    private void _processIntent(Intent intent) {
        if (intent == null)
            return;

        String msg = intent.getStringExtra("msg");

        Toast.makeText(
                this,
                String.format("MyServiceActivity._processIntent msg : %s", msg),
                Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        _processIntent(intent);
        super.onNewIntent(intent);
    }
}
