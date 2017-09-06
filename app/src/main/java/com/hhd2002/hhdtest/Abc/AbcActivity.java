package com.hhd2002.hhdtest.Abc;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.hhd2002.hhdtest.R;

public class AbcActivity
        extends AppCompatActivity {

    private EditText edit;
    private Button btn;
    private TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_abc);

        this.edit = (EditText) this.findViewById(R.id.edit);
        this.btn = (Button) this.findViewById(R.id.btn);
        this.tv = (TextView) this.findViewById(R.id.tv);
        this.tv.setText(Html.fromHtml("<font color=\"#80ff0000\">abc</font>"));
        this.setTitle(Html.fromHtml("<font color=\"#80ff0000\">abc</font>"));

        btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String str = edit.getText().toString();
                tv.setText(str);
            }
        });
    }
}

