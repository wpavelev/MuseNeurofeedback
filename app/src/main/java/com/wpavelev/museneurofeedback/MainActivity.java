package com.wpavelev.museneurofeedback;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.choosemuse.libmuse.ConnectionState;
import com.choosemuse.libmuse.LibmuseVersion;
import com.choosemuse.libmuse.MuseDataPacketType;

import java.lang.ref.WeakReference;
import java.util.Random;

import static com.wpavelev.museneurofeedback.Constants.FRAMERATE;

public class MainActivity extends AppCompatActivity implements IMainActivity{

    private static final String TAG = "MainActivity";
    
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    Constants settings = Constants.getInstance();

    private boolean isPretestRunning = false;
    private boolean isSessionRunning = false;


    private ArrayAdapter<String> spinnerAdapter; //Verschieden Muse-Geräte


    double[] relativeAlphaBuffer = new double[6];
    double[] absoluteAlphaBuffer = new double[6];

    Mean[] preTestItem = new Mean[4];

    private double generalMean = 0;
    private double currentMean = 0;

    MuseSystem muse;
    private int meanCalcTimeCounter = 0;
    private int sessionTimeCounter = 0;
    private int lowerTimeCounter = 0;
    private int upperTimeCounter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
       
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        
        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));


        Log.i(TAG, "LibMuse version=" + LibmuseVersion.instance().getString());

        WeakReference<MainActivity> weakActivity =
                new WeakReference<MainActivity>(this);



        ensurePermissions();
        loadingSharedPref();


        final TextView statusText = findViewById(R.id.con_status);
        statusText.setText("Kein Muse-Headset gefunden!");

        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = pInfo.versionName;
            TextView tv_version = findViewById(R.id.tv_version);
            tv_version.setText("version: " + version);

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        muse = new MuseSystem(this, weakActivity);


    }


    private void loadingSharedPref() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        settings.setLowerTime(Integer.parseInt(sharedPreferences.getString(getString(R.string.key_lower_time), "" + 20)));
        settings.setLowerLimit(Integer.parseInt(sharedPreferences.getString(getString(R.string.key_lower_limit), "" + 20)));
        settings.setUpperTime(Integer.parseInt(sharedPreferences.getString(getString(R.string.key_upper_time), "" + 20)));
        settings.setUpperLimit(Integer.parseInt(sharedPreferences.getString(getString(R.string.key_upper_limit), "" + 75)));
        settings.setSessionTime(Integer.parseInt(sharedPreferences.getString(getString(R.string.key_session_time), "" + 900)));
        settings.setPretestTime(Integer.parseInt(sharedPreferences.getString(getString(R.string.key_pretest_time), "" + 300)));
        settings.setUseAbsoluteAlpha(sharedPreferences.getBoolean(getString(R.string.key_absolute_alpha), false));

        settings.setUseShortTermMean(sharedPreferences.getBoolean(getString(R.string.key_short_term_mean), false));
        settings.setShortTermMean(Double.parseDouble(sharedPreferences.getString(getString(R.string.key_short_term_mean_value), "" + 2)));

        settings.setUseManualBaseline(sharedPreferences.getBoolean(getString(R.string.key_manual_baseline), false));
        settings.setManualBaseline(Double.parseDouble(sharedPreferences.getString(getString(R.string.key_manual_baseline_value), "" + 0.5)));


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.settings:
                startActivity(new Intent(this, Settings.class));
                break;

            case R.id.discon:

                //TODO DIsconnect

                break;

            case R.id.finish:
                finish();
                break;


            case R.id.resetdata:
                resetPretestData();
                break;

            case R.id.soundcheck:
                startActivity(new Intent(this, SoundCheck.class));
                break;

        }


        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        loadingSharedPref();



    }

    @Override
    protected void onPause() {
        super.onPause();
        muse.stopListening();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        // TODO: 30.05.2018
        //savingSharedPref();
//        highSound.release();
//        deepSound.release();
//        finishSound.release();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // do something on back.
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }


    private void ensurePermissions() {

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            DialogInterface.OnClickListener buttonListener =
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                                    0);
                        }
                    };


            AlertDialog introDialog = new AlertDialog.Builder(this)
                    .setTitle("Berechtigung Benötigt")
                    .setMessage("Für Bluethooth Low Energy Mode wird eine Berechtigung des Standortes gebraucht! ")
                    .setPositiveButton("OK", buttonListener)
                    .create();
            introDialog.show();
        }


        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {


            DialogInterface.OnClickListener buttonListener =
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                    0);
                        }
                    };


            AlertDialog introDialog = new AlertDialog.Builder(this)
                    .setTitle("Berechtigung Benötigt")
                    .setMessage("Zum Speichern der Daten zugriff auf den Speicher notwendig. ")
                    .setPositiveButton("OK", buttonListener)
                    .create();
            introDialog.show();
        }





    }




    private void setGeneralMean(double mean) {
        Log.d(TAG, "set generalMean = " + mean);
        generalMean = mean;
    }

    private void resetPretestData() {
//
//        if (getLogger().isLogReady()) {
//            getLogger().stopLogging();
//        }

        //TODO
        // logger = null;

        for (Mean mean : preTestItem) {
            mean.resetValues();
        }
        setGeneralMean(0);


//     TODO: Progressbar
//
//        preTestTimeCounter = 0;
//        sessionTimeCounter = 0;
//
//        //resetGraph(0);


        ProgressBar bar = findViewById(R.id.progressBar2);
        bar.setProgress(0);
        bar.setMax(100);
    }

    private boolean isCorrectSignal(int index) {
        boolean isNan = Double.isNaN(relativeAlphaBuffer[index]);
        if (isNan) {
            return false;
        } else {
            return true;
        }
    }



    private void updateAlpha(double[] data) {


        double[] workingBuffer = data;


        //TODO: getLogger().writePacket(absoluteAlphaBuffer);

        //<editor-fold desc="UI Init">
        TextView[] tv_mean = new TextView[4];
        TextView[] tv_connectionIndicator = new TextView[4];
        TextView[] tv_current = new TextView[4];

        TextView allMean = findViewById(R.id.mean);
        TextView allCurrent = findViewById(R.id.current);

        TextView tv_lowerLimit = findViewById(R.id.tv_lowerlimit);
        TextView tv_upperLimit = findViewById(R.id.tv_upperlimit);

        tv_mean[0] = findViewById(R.id.alphaAverage0);
        tv_mean[1] = findViewById(R.id.alphaAverage1);
        tv_mean[2] = findViewById(R.id.alphaAverage2);
        tv_mean[3] = findViewById(R.id.alphaAverage3);


        tv_connectionIndicator[0] = findViewById(R.id.alpha0);
        tv_connectionIndicator[1] = findViewById(R.id.alpha1);
        tv_connectionIndicator[2] = findViewById(R.id.alpha2);
        tv_connectionIndicator[3] = findViewById(R.id.alpha3);


        tv_current[0] = findViewById(R.id.tv_current0);
        tv_current[1] = findViewById(R.id.tv_current1);
        tv_current[2] = findViewById(R.id.tv_current2);
        tv_current[3] = findViewById(R.id.tv_current3);

        ProgressBar bar = findViewById(R.id.progressBar2);
        //</editor-fold>


        double currentSum = 0;
        currentMean = 0;
        int currentSignalCounter = 0;

        //Rechne durchschnitt oder zähle nicht verbundenen signale
        for (int i = 0; i < 4; i++) {
            if (isCorrectSignal(i)) {
                currentSum += workingBuffer[i];
                currentSignalCounter++;
                currentMean = currentSum / currentSignalCounter;
            }
        }


        //wenn Pretest begonnen wurde / Pretest läuft
        if (isPretestRunning) {
            //<editor-fold desc="Pretest">

            //Progressbar aktualisieren
            bar.setMax((int) settings.getPreTestLength());
            bar.setProgress(meanCalcTimeCounter);


            //Alle Summen ablaufen
            for (int i = 0; i < preTestItem.length; i++) {

                //Wenn Signal der Elektrode i gut ist, diesen Wert zu der Summe addieren und den
                //Durchschnitt berechnen
                if (isCorrectSignal(i)) {
                    preTestItem[i].newValue(workingBuffer[i]);
                }

            }

            //Summe aller 4 Elektroden berechnen (Durchschnitsswerte)
            Mean preTest = new Mean();
            for (Mean mean : preTestItem) {
                preTest.newValue(mean.getMean());
            }

            //Allgemeinen Durchschnitt berechnen und speichern
            setGeneralMean(preTest.getMean());


            //TODO: echten Countdown einfügen
            // Timer nooch nicht abgelaufen?
            if (meanCalcTimeCounter < settings.getPreTestLength()) {
                meanCalcTimeCounter++;


            } else {            //Wenn Time abgelaufen, Pre-Test ausschalten
                Button pretestButton = findViewById(R.id.start_calculate_mean);
                pretestButton.setText("Pre-Test Starten!");
                Log.d(TAG, "Pre-Test beendet");
                isPretestRunning = false;

                // TODO: 30.05.2018 enable SessionButton
                // TODO: 30.05.2018 Logger puase record


            }
            //</editor-fold>
        } else if (isSessionRunning) {
            //<editor-fold desc="Session">



            bar.setMax((int) settings.getSessionLength());
            bar.setProgress(sessionTimeCounter);


            Log.d(TAG, "currentMean " + currentMean);

            //Untere Schwelle

            double configLowerLimit = generalMean * (double) settings.getLowerLimit() / 100;

            String text = configLowerLimit + "";
            if (text.length() > 5) {
                text = text.substring(0, 5);
            }
            tv_lowerLimit.setText("Untere Grenze: " + text);

            if (currentMean < configLowerLimit && lowerTimeCounter > (FRAMERATE * settings.getLowerTime())) {

                //Schwelle unterschritten, spiele Sound ab, setzte Timer zurück

                // TODO: 30.05.2018 starte Sound
                lowerTimeCounter = 0;
            } else {
                lowerTimeCounter++;
            }


            //Obere Schwelle

            double configUpperLimit = generalMean * (double) settings.getUpperLimit() / 100;
            text = configUpperLimit + "";
            if (text.length() > 5) {
                text = text.substring(0, 5);
            }
            tv_upperLimit.setText("Obere Grenze: " + text);
            Log.d(TAG, "configUperLimit" + configUpperLimit);

            if (currentMean > configUpperLimit && upperTimeCounter > (FRAMERATE * settings.getUpperTime())) {

                //Schwelle überschritte, spiele Sound ab, setzte Time zurück

                // TODO: 30.05.2018 starte Sound
                upperTimeCounter = 0;

            } else {
                upperTimeCounter++;
            }


            if (sessionTimeCounter < settings.getSessionLength()) { //Timer nooch nicht abgelaufen?
                sessionTimeCounter++;
            } else { //Wenn Time abgelaufen, Test ausschalten
                // TODO: 30.05.2018 finish sound?
                // TODO: 30.05.2018 pause logger
                // TODO: 30.05.2018 disable sessionButton
                isSessionRunning = false;

                Button sessionButton = findViewById(R.id.start_session);
                sessionButton.setText("Start Session");
            }


            //</editor-fold>
        }


        //<editor-fold desc="UI Change">

        //CHART


        // TODO: 30.05.2018 addGraphData



        //Momentane Alpha Werte
        //Elektroden Verbindung
        for (int i = 0; i < tv_current.length; i++) {
            String text = "" + workingBuffer[i];
            if (text.length() > 5) {
                text = text.substring(0, 5);
            }
            tv_current[i].setText(text);
        }





        //Durchschnitt der Alpha Werte
        for (int i = 0; i < tv_mean.length; i++) {
            String text = "" + preTestItem[i].getMean();
            if (text.length() > 5) {
                text = text.substring(0, 5);
            }
            tv_mean[i].setText(text);
        }


        String text = "" + currentMean;
        if (text.length() > 5) {
            text = text.substring(0, 5);
        }

        allCurrent.setText("Aktuellen Werte - µ: " + text);

        text = "" + generalMean;
        if (text.length() > 5) {
            text = text.substring(0, 5);
        }

        allMean.setText("Durchschnittswerte im Pretest - µ: " + text);

        //Elektroden Verbindung
        for (int i = 0; i < tv_connectionIndicator.length; i++) {
            //tv_alphas[i].setText("" + workingBuffer[i]);
            if ((Double.isNaN(relativeAlphaBuffer[i]))) {
                tv_connectionIndicator[i].setBackgroundColor(getResources().getColor(R.color.red));
            } else {
                tv_connectionIndicator[i].setBackgroundColor(getResources().getColor(R.color.green));
            }

        }
        //</editor-fold>


    }



    private void generateTestData() {

        Random r = new Random();

        for (int i = 0; i < 4; i++) {
            double randomValue = r.nextDouble();
            relativeAlphaBuffer[i] = randomValue;
        }



    }


    @Override
    public void updateStatus(ConnectionState connectionState) {

        if (connectionState == ConnectionState.DISCONNECTED) {
            Log.i(TAG, "Muse disconnected:" + muse.getName());

            ImageButton button = findViewById(R.id.connect);
            button.setColorFilter(getResources().getColor(R.color.darkgray));


            // TODO: 30.05.2018  isSessionButtonEnabled = buttonEnable(false, R.id.start_session);
            // TODO: 30.05.2018  isPreTestButtonEnabled = buttonEnable(false, R.id.start_calculate_mean);


            this.muse = null;
        } else if (connectionState == ConnectionState.CONNECTING) {

        } else if (connectionState == ConnectionState.CONNECTED) {

            // TODO: 30.05.2018  isPreTestButtonEnabled = buttonEnable(true, R.id.start_calculate_mean);

            ImageButton button = findViewById(R.id.connect);
            button.setColorFilter(getResources().getColor(R.color.green));

        }


    }



    @Override
    public void setData(double[] data, MuseDataPacketType type) {

        if (type == MuseDataPacketType.ALPHA_RELATIVE) {

        }



    }




    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case 0:
                    return new MuseConnectFragment();
                case 1:
                    return new InfoFragment();
                case 2:
                    return new GraphFragment();
                default:
                    return new MuseConnectFragment();
                        
                        
            }
            
            
        }

        @Override
        public int getCount() {
            return 3;
        }
    }
}
