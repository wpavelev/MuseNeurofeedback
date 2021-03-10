package com.wpavelev.museneurofeedback;

import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SoundCheck extends AppCompatActivity {

    private MediaPlayer highSound, deepSound, finishSound;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sound_check);

        highSound = MediaPlayer.create(this, R.raw.high_sound);
        deepSound = MediaPlayer.create(this, R.raw.deep_sound);
        finishSound = MediaPlayer.create(this, R.raw.finish);

        Button testsound1 = findViewById(R.id.testsound1);
        testsound1.setOnClickListener(new Click());

        Button testsound2 = findViewById(R.id.testsound2);
        testsound2.setOnClickListener(new Click());

        Button testsound3 = findViewById(R.id.testsound3);
        testsound3.setOnClickListener(new Click());



    }


    class Click implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {

                case R.id.testsound1:
                    finishSound.start();
                    break;

                case R.id.testsound2:
                    highSound.start();
                    break;

                case R.id.testsound3:
                    deepSound.start();
                    break;

            }
        }
    }



}
