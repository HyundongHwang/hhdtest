package com.hhd2002.hhdtest.BeepVibrateTest;

import java.io.*;

import android.content.*;
import android.content.res.*;
import android.media.*;
import android.os.*;
import android.support.v7.app.AppCompatActivity;
import android.util.*;
import android.view.*;
import android.widget.*;

import com.hhd2002.hhdtest.R;

public class BeepVibrateTestActivity
        extends AppCompatActivity {

    private Button _btn_sound_pool;
    private Button _btn_audio_manager;
    private Button _btn_vibrate;

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_beep_vibratetes);

        _btn_sound_pool = (Button) this.findViewById(R.id.btn_sound_pool);
        _btn_audio_manager = (Button) this.findViewById(R.id.btn_audio_manager);
        _btn_vibrate = (Button) this.findViewById(R.id.btn_vibrate);

        _btn_sound_pool.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                SoundPool sp = new SoundPool(20, AudioManager.STREAM_MUSIC, 0);
                AssetManager am = getAssets();
                AssetFileDescriptor afd = null;

                try {
                    afd = am.openFd("/res/raw/test_cbr.mp3");
                } catch (IOException e) {
                    Log.i("hhd", "e : " + e);
                }

                int id = sp.load(afd, 1);
                sp.play(id, 1, 1, 0, 0, 1);
            }

        });

        _btn_audio_manager.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                AudioManager am = (AudioManager) getSystemService(AUDIO_SERVICE);
                am.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD);
            }
        });

        _btn_vibrate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(500);
            }
        });
    }
}
