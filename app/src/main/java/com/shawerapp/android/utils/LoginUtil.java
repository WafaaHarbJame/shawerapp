package com.shawerapp.android.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by john.ernest on 05/10/2017.
 */

public class LoginUtil {

    private final String SHARED_PREFS_NAME = "com.shawerapp.android.LOGIN_STATE";

    private Context context;

    private SharedPreferences preferences;

    public LoginUtil(Context context) {
        this.context = context;

        preferences = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
    }

    public void setUserLoggedIn(boolean loggedIn) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("shawerapp.LOGGED_IN", loggedIn);
        editor.apply();
    }

    public boolean isLoggedIn() {
        return preferences.getBoolean("shawerapp.LOGGED_IN", false);
    }

    public void setUserID(String userID) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("shawerapp.USER_ID", userID);
        editor.apply();
    }

    public String getUserID() {
        return preferences.getString("shawerapp.USER_ID", null);
    }

    public void setPhoneNumber(String phoneNumber) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("shawerapp.PHONE_NUMBER", phoneNumber.replace("+", ""));
        editor.apply();
    }

    public String getPhoneNumber() {
        return preferences.getString("shawerapp.PHONE_NUMBER", null);
    }

    public void setUserRole(String role) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("shawerapp.ROLE", role);
        editor.apply();
    }

    public String getUserRole() {
        return preferences.getString("shawerapp.ROLE", null);
    }

    public void setUsername(String username) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("shawerapp.USERNAME", username);
        editor.apply();
    }

    public String getUsername() {
        return preferences.getString("shawerapp.USERNAME", null);
    }

    public void clear() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();
    }
}
