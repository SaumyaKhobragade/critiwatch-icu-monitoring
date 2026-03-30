package com.example.critiwatch.services;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    private static final String PREF_NAME = "critiwatch_session";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";

    private final SharedPreferences preferences;

    public SessionManager(Context context) {
        preferences = context.getApplicationContext()
                .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void createLoginSession(String userName, String userEmail, String userRole) {
        preferences.edit()
                .putBoolean(KEY_IS_LOGGED_IN, true)
                .apply();
    }

    public boolean isLoggedIn() {
        return preferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public String getUserName() {
        return "";
    }

    public String getUserEmail() {
        return "";
    }

    public String getUserRole() {
        return "";
    }

    public void clearSession() {
        preferences.edit().clear().apply();
    }
}
