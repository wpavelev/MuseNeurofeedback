package com.wpavelev.museneurofeedback;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class Settings extends PreferenceActivity implements Preference.OnPreferenceChangeListener  {

    String TAG = "Settings";

    int upperLimit, upperTime, lowerLimit, lowerTime, pretestTime, sessionTime;
    boolean useAbsoluteAlpha;

    // TODO: 08.05.2018 backbutton / zurück taste
    // TODO: 08.05.2018 ok button

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        Intent intent = getIntent();


    }



    @Override
    public void onBackPressed(){
       finish();
    }


    @Override
    public boolean onPreferenceChange(Preference preference, Object value) {

        if (preference instanceof CheckBoxPreference) {

            ((CheckBoxPreference) preference).setChecked((Boolean) value);
        } else if (preference instanceof ListPreference) {
            // For list preferences, look up the correct display value in
            // the preference's 'entries' list.
            ListPreference listPreference = (ListPreference) preference;
            int index = listPreference.findIndexOfValue(value.toString());

            // Set the summary to reflect the new value.
            preference.setSummary(
                    index >= 0
                            ? listPreference.getEntries()[index]
                            : null);

        } else {
            preference.setSummary(value.toString());

        }

        return true;

    }
}
