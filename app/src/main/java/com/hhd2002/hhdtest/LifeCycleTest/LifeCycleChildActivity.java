package com.hhd2002.hhdtest.LifeCycleTest;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;


public class LifeCycleChildActivity
        extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            Log.i("hhddebug", "extras.getString(a) : " + extras.getString("a"));
            Log.i("hhddebug", "extras.getString(b) : " + extras.getString("b"));
            Log.i("hhddebug", "extras.getString(c) : " + extras.getString("c"));
        }

        if (savedInstanceState != null) {
            Log.i("hhddebug", "onCreate savedInstanceState.getString(1) : " + savedInstanceState.getString("1"));
            Log.i("hhddebug", "onCreate savedInstanceState.getString(2) : " + savedInstanceState.getString("2"));
            Log.i("hhddebug", "onCreate savedInstanceState.getString(3) : " + savedInstanceState.getString("3"));
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString("1", "하나");
        outState.putString("2", "둘");
        outState.putString("3", "셋");
    }
}
