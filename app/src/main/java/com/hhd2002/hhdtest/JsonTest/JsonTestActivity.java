package com.hhd2002.hhdtest.JsonTest;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.hhd2002.androidbaselib.IHhdSampleActivity;

import java.io.IOException;
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

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(JsonGenerator.Feature.QUOTE_FIELD_NAMES, false)
                .configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true)
                .configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL, true)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

        String str = "";
        AAA2 obj3 = null;

        try {
            str = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        try {
            obj3 = mapper.readValue(str, AAA2.class);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }


        String str2 = "{\n:    a\n:        b\n:\tc\n:\t\td\n:}";
        Log.e("", str2);

        int d = 0;

    }

    @Override
    public String getSampleDesc() {
        return "json 샘플, ObjectMapper, writeValueAsString, writerWithDefaultPrettyPrinter, readValue";
    }
}

