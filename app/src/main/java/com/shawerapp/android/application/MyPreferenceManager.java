package com.shawerapp.android.application;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class MyPreferenceManager {

    private static MyPreferenceManager instance;
    private SharedPreferences SP;

    public MyPreferenceManager(Context mContext) {
        SP = PreferenceManager.getDefaultSharedPreferences(mContext);
    }

    public static MyPreferenceManager getInstance(Context context) {
        if (instance == null) {
            synchronized (MyPreferenceManager.class) {
                if (instance == null)
                    instance = new MyPreferenceManager(context);
            }
        }
        return instance;
    }

    public SharedPreferences.Editor getEditor() {
        return SP.edit();
    }

    public boolean contains(String key) {
        if(SP.contains(key)) {
            return true;
        } else {
            return false;
        }
    }
    public boolean putString(String key, String value) {
        return getEditor().putString(key, value).commit();
    }

    public String getString(String key, String defValue) {
        return SP.getString(key, defValue);
    }
    public boolean putLong(String key, Double value) { return getEditor().putLong(key, Double.doubleToLongBits(value)).commit(); }
    public long getLong(String key, long defValue) { return SP.getLong(key,defValue);}
    public void putInt(String key, int value) {
        getEditor().putInt(key, value).commit();
    }

    public int getInt(String key, int defValue) {
        return SP.getInt(key, defValue);
    }

    public void putBoolean(String key, boolean value) {
        getEditor().putBoolean(key, value).commit();
    }

    public boolean getBoolean(String key, boolean defValue) {
        return SP.getBoolean(key, defValue);
    }


    public void remove(String key) {
        getEditor().remove(key).commit();
    }

    public void clearPreferences() {
        getEditor().clear().commit();
    }
}
