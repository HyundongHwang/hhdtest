package com.hhd2002.hhdtest.FcmTest;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.google.firebase.iid.FirebaseInstanceId;
import com.hhd2002.androidbaselib.HhdSampleUiHelper;
import com.hhd2002.androidbaselib.IHhdSampleActivity;
import com.hhd2002.androidbaselib.log.HhdLog;
import com.hhd2002.hhdtest.R;
import com.hhd2002.icndb.IcndbApis;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;

/**
 * Created by hhd on 2017-06-25.
 */

public class FcmTestActivity
        extends AppCompatActivity
        implements IHhdSampleActivity {

    private String _token;
    private HhdSampleUiHelper _uiHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _processIntent(this.getIntent());
        _uiHelper = new HhdSampleUiHelper(this);

        _uiHelper.addSimpleBtn("getToken", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _token = FirebaseInstanceId.getInstance().getToken();
                HhdLog.d("addSimpleBtn getToken _token : %s", _token);
                _uiHelper.writeLog(String.format("addSimpleBtn getToken _token : %s", _token));
            }
        });

        _uiHelper.addSimpleBtn("send push notification", new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                @SuppressLint("StaticFieldLeak")
                AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... voids) {

                        try {
                            Thread.sleep(5000);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        IFcmApis api = IFcmApis.create();
                        IFcmApis.SendRequest req = new IFcmApis.SendRequest();
                        req.to = _token;
                        HashMap<String, String> data = new HashMap<>();
                        req.data = data;
                        data.put("msg", "안녕하세요");

                        Call<IFcmApis.SendResponse> call = api.PostSend(req);

                        try {
                            IFcmApis.SendResponse res = call.execute().body();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        return null;
                    }
                };

                task.execute();
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        _processIntent(intent);
    }

    private void _processIntent(Intent intent) {
        if (intent.getExtras() == null)
            return;



        String from = intent.getExtras().getString("from");
        _uiHelper.writeLog(String.format("_processIntent from : %s", from));

        String data = intent.getExtras().getString("data");
        _uiHelper.writeLog(String.format("_processIntent data : %s", data));

        String notification = intent.getExtras().getString("notification");
        _uiHelper.writeLog(String.format("_processIntent notification : %s", notification));
    }

    @Override
    public String getSampleDesc() {
        return "Fcm, push service, as notification, as data";
    }
}
