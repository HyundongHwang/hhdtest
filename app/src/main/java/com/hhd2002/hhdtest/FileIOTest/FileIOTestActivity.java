package com.hhd2002.hhdtest.FileIOTest;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.hhd2002.androidbaselib.HhdFileUtils;
import com.hhd2002.androidbaselib.HhdStringUtils;
import com.hhd2002.androidbaselib.SampleUI.HhdSampleUiHelper;
import com.hhd2002.hhdtest.R;

/**
 * Created by hhd on 2017-07-05.
 */
public class FileIOTestActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        HhdSampleUiHelper uiHelper = new HhdSampleUiHelper(this);
        uiHelper.addSimpleBtn("테스트", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                byte[] assetsBuf = HhdFileUtils.readAssetsAsBytes(context, "my.txt");
                String assetsStr = HhdStringUtils.convertBytesToUtf8String(assetsBuf);
                uiHelper.writeLog("assetsStr : " +  assetsStr);

                String extFilePath = HhdFileUtils.getAbsolutePathInExtFilesDir(context, "my.txt");
                HhdFileUtils.writeToFile(assetsStr, extFilePath);

                String readFileStr = HhdFileUtils.readStringFromFile(extFilePath);
                uiHelper.writeLog("readFileStr : " +  readFileStr);

                byte[] bufRaw = HhdFileUtils.readRawAsBytes(context, R.raw.my);
                String rawStr = HhdStringUtils.convertBytesToUtf8String(bufRaw);
                uiHelper.writeLog("rawStr : " +  rawStr);
            }
        });
    }
}
