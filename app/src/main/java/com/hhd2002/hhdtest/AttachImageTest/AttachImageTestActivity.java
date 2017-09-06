package com.hhd2002.hhdtest.AttachImageTest;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.hhd2002.androidbaselib.SampleUI.HhdSampleUiHelper;

import java.io.File;

public class AttachImageTestActivity
        extends AppCompatActivity {


    private static final int CALL_CAMERA2 = 1;
    private static final int CALL_GALLERY = 2;
    private static File tempFile;


    private HhdSampleUiHelper sampleHelper;
    private ImageView img;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sampleHelper = new HhdSampleUiHelper(this);

        sampleHelper.addSimpleBtn("ACTION_IMAGE_CAPTURE EXTRA_OUTPUT", (v) -> {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile));
            startActivityForResult(intent, CALL_CAMERA2);
        });

        sampleHelper.addSimpleBtn("ACTION_PICK", (v) -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, CALL_GALLERY);
        });

        img = new ImageView(this);
        img.setAdjustViewBounds(true);
        sampleHelper.addView(img, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        try {
            tempFile = File.createTempFile("_temp_", "_temp_", this.getExternalCacheDir());
        } catch (Exception e) {
            Log.i("hhddebug", "AttachImageTestActivity.onCreate e : " + e);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case CALL_CAMERA2: {
                    Uri fileUri = Uri.fromFile(tempFile);
                    Glide.with(this).load(fileUri).into(img);
                    break;
                }
                case CALL_GALLERY: {
                    Uri uri = data.getData();
                    Glide.with(this).load(uri).into(img);
                    break;
                }
            }
        }
    }
}
