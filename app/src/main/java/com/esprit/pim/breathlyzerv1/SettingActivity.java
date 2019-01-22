package com.esprit.pim.breathlyzerv1;

import android.app.Activity;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class SettingActivity extends AppCompatPreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener{
    String taxinumber;
    String alcoth;
    SharedPreferences pref;
    String kitpwsd;
    protected static final int SUCCESS_CONNECT = 0;


        @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        PreferenceManager.setDefaultValues(SettingActivity.this, R.xml.activity_setting, false);
        addPreferencesFromResource(R.xml.activity_setting);
        pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPref, String key) {
//        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
//        String restoredText = prefs.getString("ALTHRES", null);
//
//        EditTextPreference alnum = (EditTextPreference) findPreference("thes");
//       alnum.setText(restoredText);



//        EditTextPreference alco = (EditTextPreference) findPreference("alcoholThreshold");
//        alcoth = String.valueOf(alco.getText().toString());


        EditTextPreference tnum = (EditTextPreference) findPreference("taxinumb");
        taxinumber = String.valueOf(tnum.getText().toString());
      //  Log.i("tnumMMMMMMMMMMMM", "tellll*SSSSSETTINGACTIVITY ****///***//::::" + taxinumber);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("taxin", taxinumber);
        editor.commit();

    }

    @Override
    protected void onResume() {
        super.onResume();

        // Start the force toggle


        // Set up a listener whenever a key changes
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Unregister the listener whenever a key changes
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);


    }



//    @Override
//    public boolean onPreferenceChange(Preference preference, Object newValue) {
//        EditTextPreference alnum = (EditTextPreference) findPreference("limit");
//        String alcohollim = String.valueOf(alnum.getText().toString());
//
//        EditTextPreference tnum = (EditTextPreference) findPreference("taxinumb");
//        taxinumber = String.valueOf(tnum.getText().toString());
//
//        SharedPreferences.Editor editor = getPreferences(MODE_PRIVATE).edit();
//        editor.putString("taxin", taxinumber);
//        editor.commit();
//
//        return false;
//    }
}

