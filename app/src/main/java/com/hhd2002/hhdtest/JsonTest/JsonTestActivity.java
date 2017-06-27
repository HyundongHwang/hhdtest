package com.hhd2002.hhdtest.JsonTest;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hhd2002.androidbaselib.HhdSampleUiHelper;
import com.hhd2002.androidbaselib.IHhdSampleActivity;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by hhd2002 on 2014. 7. 23..
 */
public class JsonTestActivity
        extends AppCompatActivity
        implements IHhdSampleActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        HhdSampleUiHelper hhdSampleUiHelper = new HhdSampleUiHelper(this);

        AAA obj = new AAA();
        obj.overwrite_d_day = "asdfjalsf";
        obj.overwrite_abc_day = "qrewqrfjalsf";
        obj.bbb = 12312334;
        obj.ccc = true;

        obj.ddd = new ArrayList<String>();
        obj.ddd.add("zvxcxzvc");
        obj.ddd.add("zvxcxzasdfavc");
        obj.ddd.add("zvxcxzwtrwtvc");

        obj.eee = new ArrayList<BBB>();

        BBB obj2 = new BBB();
        obj.eee.add(obj2);
        obj.eee.add(obj2);
        obj.eee.add(obj2);

        obj2.hhh = "asdfasfas";
        obj2.iii = 123123;
        obj2.jjj = true;

        obj.fff = new HashMap<String, String>();
        obj.fff.put("asdfasdf", "ㅁㅎㄹㅇㅎㅇㄹㄴㅎㅇㄴㅎ");
        obj.fff.put("asdfasdf", "ㅁㅎㄹㅇㅎㅇㄹㄴㅎㅇㄴㅎ");
        obj.fff.put("asdfasdf", "ㅁㅎㄹㅇㅎㅇㄹㄴㅎㅇㄴㅎ");
        obj.fff.put("asdfasdf", "ㅁㅎㄹㅇㅎㅇㄹㄴㅎㅇㄴㅎ");

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String objStr = gson.toJson(obj);
        hhdSampleUiHelper.writeLog(String.format("objStr : %s", objStr));

        String obj2Str = gson.toJson(obj2);
        hhdSampleUiHelper.writeLog(String.format("obj2Str : %s", obj2Str));

        AAA aaa = gson.fromJson(objStr, AAA.class);
        hhdSampleUiHelper.writeLog(String.format("aaa : %d", aaa.hashCode()));

        BBB bbb = gson.fromJson(obj2Str, BBB.class);
        hhdSampleUiHelper.writeLog(String.format("bbb : %d", bbb.hashCode()));
    }

    @Override
    public String getSampleDesc() {
        return "json 샘플, ObjectMapper, writeValueAsString, writerWithDefaultPrettyPrinter, readValue";
    }
}

