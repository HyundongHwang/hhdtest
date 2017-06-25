package com.hhd2002.hhdtest.LifeCycleTest;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.hhd2002.androidbaselib.IHhdSampleActivity;

public class LifeCycleTestActivity
        extends AppCompatActivity
        implements IHhdSampleActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Button button = new Button(this);
        setContentView(button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LifeCycleTestActivity.this, LifeCycleChildActivity.class);
                intent.putExtra("a", "에이");
                intent.putExtra("b", "비이");
                intent.putExtra("c", "씨이");
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public String getSampleDesc() {
        return "라이프사이클 테스트, 부모 액티비티";
    }
}
