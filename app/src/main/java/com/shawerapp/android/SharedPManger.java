package com.shawerapp.android;

import android.content.Context;
import android.content.SharedPreferences;

import com.shawerapp.android.Payment.Constants;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by A7mad28 on 2015-10-28.
 */
public class SharedPManger {
    SharedPreferences preferences;


    public SharedPManger(Context context) {
        preferences = context.getSharedPreferences(Constants.key_file, MODE_PRIVATE);

       // preferences = context.getSharedPreferences("smartZoneSettings", 0);
    }


    public void SetData(String key, String value) {

        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.commit();

    }

    public void SetData(String key, int value) {

        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(key, value);
        editor.commit();

    }

    public void SetData(String key, float value) {

        SharedPreferences.Editor editor = preferences.edit();
        editor.putFloat(key, value);
        editor.commit();

    }

    public void SetData(String key, boolean value) {

        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(key, value);
        editor.commit();

    }

    public String getDataString(String key) {
        return preferences.getString(key, null);
    }

    public String getDataString(String key, String the_default) {
        return preferences.getString(key, the_default);
    }

    public int getDataInt(String key) {
        return preferences.getInt(key, 0);
    }

    public int getDataInt(String key, int defult) {
        return preferences.getInt(key, defult);
    }

    public float getDataFloat(String key) {
        return preferences.getFloat(key, 0);
    }

    public boolean getDataBool(String key) {
        return preferences.getBoolean(key, false);
    }

    public boolean getDataBool(String key, boolean The_default) {
        return preferences.getBoolean(key, The_default);
    }
}
