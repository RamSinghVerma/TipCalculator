package com.example.keshav.tipcalculator;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;

public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
    private String TAG="SettingsFragment";
    private boolean rememberPercent;
    private SharedPreferences prefs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);
        //PreferenceManager.setDefaultValues(this,R.xml.preferences,false);
        prefs=PreferenceManager.getDefaultSharedPreferences(getActivity());
        Log.i(TAG,"On create method...............");
    }

    @Override
    public void onResume() {
        super.onResume();
        // CheckedPreference status on basis of key
        rememberPercent=prefs.getBoolean("pref_remember_percent",true);

        this.setDefaultPercentRemember(rememberPercent);
        prefs.registerOnSharedPreferenceChangeListener(this);
        Log.i(TAG,"On resume method...............");
    }

    public void setDefaultPercentRemember(boolean rememberPercent){
      /*  Preference defaultPreference=findPreference("pref_remember_percent");
        if(rememberPercent)
            defaultPreference.setEnabled(false);
        else
            defaultPreference.setEnabled(true);*/

        Log.i(TAG,"On default percent remember method...............");
    }

    @Override
    public void onPause() {
        prefs.unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
        Log.i(TAG,"On pause method...............");
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals("pref_remember_percent"))
            rememberPercent=sharedPreferences.getBoolean(key,true);

        Log.i(TAG,"On SharedPreferenceChanged method...............");
        this.setDefaultPercentRemember(rememberPercent);
    }

}
