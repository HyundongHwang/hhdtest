package com.hhd2002.hhdtest.JokeFinderTest;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.hhd2002.androidbaselib.HhdSampleUiHelper;
import com.hhd2002.androidbaselib.IHhdSampleActivity;
import com.hhd2002.icndb.JokeFinder;

/**
 * Created by hhd on 2017-06-16.
 */

public class JokeFinderTestActivity
        extends AppCompatActivity
        implements IHhdSampleActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        HhdSampleUiHelper hhdSampleUiHelper = new HhdSampleUiHelper(this);

        hhdSampleUiHelper.addSimpleBtn("jokeFinder.GetJoke", (View v) -> {
            JokeFinder jokeFinder = new JokeFinder();
            jokeFinder.GetJoke("Xavier", "Ducrohet", (String jokeStr) -> {
                JokeFinderTestActivity.this.runOnUiThread(() -> {
                    hhdSampleUiHelper.writeLog(jokeStr);
                });
            });
        });
    }

    @Override
    public String getSampleDesc() {
        return null;
    }
}
