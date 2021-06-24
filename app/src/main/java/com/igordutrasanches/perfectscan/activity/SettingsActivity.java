package com.igordutrasanches.perfectscan.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatDelegate;
import android.view.MenuItem;
import android.widget.Toast;

import com.igordutrasanches.perfectscan.R;
import com.igordutrasanches.perfectscan.compoments.Key;

public class SettingsActivity
        extends PreferenceActivity
        implements SharedPreferences.OnSharedPreferenceChangeListener {
    private AppCompatDelegate mDelegate;

    private AppCompatDelegate getDelegate() {
        if (mDelegate == null) {
            mDelegate = AppCompatDelegate.create(this, null);
        }
        return mDelegate;
    }
    public ActionBar getSupportActionBar() {
        return getDelegate().getSupportActionBar();
    }

    protected void onCreate(Bundle bundle) {
        //SettingsActivity.super.onResume();
        super.onCreate(bundle);
        try {
            this.addPreferencesFromResource(R.xml.configuracoes);
            setupActionBar();
            getSupportActionBar().setTitle(R.string.menu_settings);
            ((SwitchPreference)findPreference("continuos_mode")).setChecked(Key.getMassa(this));
            ((SwitchPreference)findPreference("invert_scan")).setChecked(Key.getDisableInvert(this));
            updateList();
        }catch (Exception x){
            Toast.makeText(this, x.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void updateList(){
        ((ListPreference)findPreference("preference_laser")).setSummary(getSumarry(Key.getLaserColor(this)));
    }

    private String getSumarry(String laserColor) {
        String color = "";
        switch (laserColor){
            case "0": color = getString(R.string.laser_default); break;
            case "1": color = getString(R.string.laser_blue_bright); break;
            case "2": color = getString(R.string.laser_blue_dark); break;
            case "3": color = getString(R.string.laser_blue_light); break;
            case "4": color = getString(R.string.laser_green_dark); break;
            case "5": color = getString(R.string.laser_green_light); break;
            case "6": color = getString(R.string.laser_orange_dark); break;
            case "7": color = getString(R.string.laser_orange_light); break;
            case "8": color = getString(R.string.laser_purple); break;
            case "9": color = getString(R.string.laser_red_dark); break;
            case "10": color = getString(R.string.laser_red_light); break;
        }
        return color;
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
           actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }
    protected void onPause() {
        super.onPause();
        this.getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener((SharedPreferences.OnSharedPreferenceChangeListener) this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            super.onBackPressed();
            //startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    protected void onResume() {
        super.onResume();
        this.getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener((SharedPreferences.OnSharedPreferenceChangeListener) this);
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String string) {
        if (string.equals("auto_focus")) {
            Key.set(string, PreferenceManager.getDefaultSharedPreferences((Context)this).getBoolean(string, true), this);
        }
        if (string.equals("continuos_mode")) {
            Key.set(string, PreferenceManager.getDefaultSharedPreferences((Context)this).getBoolean(string, false), this);
        }
        if (string.equals("invert_scan")) {
            Key.set(string, PreferenceManager.getDefaultSharedPreferences((Context)this).getBoolean(string, false), this);
        }
        if (string.equals("play_beep")) {
            Key.set(string, PreferenceManager.getDefaultSharedPreferences((Context)this).getBoolean(string, true), this);
        }
        if (string.equals("vibrate")) {
            Key.set(string, PreferenceManager.getDefaultSharedPreferences((Context)this).getBoolean(string, false), this);
        }
        if (string.equals("locutor_result_in_view")) {
            Key.set(string, PreferenceManager.getDefaultSharedPreferences((Context)this).getBoolean(string, false), this);
        }
        if (string.equals("CREATE_CODES_ENABLED")) {
            Key.set(string, PreferenceManager.getDefaultSharedPreferences((Context)this).getBoolean(string, true), this);
        }
        if (string.equals("SCANNED_CODES_ENABLED")) {
            Key.set(string, PreferenceManager.getDefaultSharedPreferences((Context)this).getBoolean(string, true), this);
        }
        if (string.equals("locutor_result")) {
            Key.set(string, PreferenceManager.getDefaultSharedPreferences((Context)this).getBoolean(string, false), this);
        }
        if (string.equals("preference_laser")) {
            Key.set(string, PreferenceManager.getDefaultSharedPreferences((Context)this).getString(string, "0"), this);
        }
        updateList();
    }
}