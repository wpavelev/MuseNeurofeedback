package com.wpavelev.museneurofeedback;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class InfoFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "InfoFragment";


    private MediaPlayer highSound, deepSound, finishSound;

    private boolean isPretestRunning = false;
    private boolean isSessionRunning = false;

    private View view;


    public InfoFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_info, container, false);

        Button startPretest = view.findViewById(R.id.start_calculate_mean);
        startPretest.setOnClickListener(this);
        Button sessionStart = view.findViewById(R.id.start_session);
        sessionStart.setOnClickListener(this);


        highSound = MediaPlayer.create(getContext(), R.raw.deep_sound);
        deepSound = MediaPlayer.create(getContext(), R.raw.high_sound);
        finishSound = MediaPlayer.create(getContext(), R.raw.finish);

        return view;


    }


    @Override
    public void onClick(View view) {


        switch (view.getId()) {
            case R.id.start_calculate_mean:

                calculateMeanClick();

                break;

            case R.id.start_session:

                sessionClick();
                break;

        }

    }


    private void buttonEnable(boolean enable, int id) {
        Button button = view.findViewById(id);
        button.setEnabled(enable);

    }


    private void calculateMeanClick() {

        Log.d(TAG, "isPretestRunning: " + isPretestRunning);
        if (!isPretestRunning) { //WENN PreTest noch nicht läuft:

            //SessionButton deaktivieren

            buttonEnable(false, R.id.start_session);

            // TODO: 30.05.2018 graph zurücksetzten und initialisieren

            // TODO: 30.05.2018 Logger initialisieren und Aufzeichnung starten

            //Session stoppen und Button deaktivieren
            isSessionRunning = false;

            //PreTest start
            isPretestRunning = true;

            //Button-Text
            Button button = view.findViewById(R.id.start_calculate_mean);
            button.setText("Stop Pre-test");

        } else {
            //WENN Pre-Test läuft: Pretest beenden

            //Wenn logger läuft, starten

            // TODO: 30.05.2018 Logger pausieren

            isPretestRunning = false;

            buttonEnable(true, R.id.start_session);


            Button button = view.findViewById(R.id.start_calculate_mean);
            button.setText("Start Pre-Test");

        }


    }


    private void sessionClick() {

        //Pre-Test Button deaktiveren
        buttonEnable(false, R.id.start_calculate_mean);

        if (!isSessionRunning) { //Wenn session noch nicht läuft: Starten
            isSessionRunning = true;


            // TODO: 30.05.2018 starte Logger

            //Text ändern
            Button button = view.findViewById(R.id.start_session);
            button.setText("Stop Session");

        } else {//Wenn session bereits läuft: stoppen

            isSessionRunning = false;

            // TODO: 30.05.2018 Logger pausieren

            Button button = view.findViewById(R.id.start_session);
            button.setText("Start Session");

            buttonEnable(true, R.id.start_calculate_mean);


        }


    }


}
