package com.example.myapplication.util;

import android.content.Context;
import android.content.SharedPreferences;

public class TokenManager {
    private static final String PREF_NAME = "auth_prefs";
    private static final String TOKEN_KEY = "access_token";

    private SharedPreferences prefs;

    public TokenManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void saveToken(String token) {
        prefs.edit().putString(TOKEN_KEY, token).apply();
    }

    public String getToken() {
        return prefs.getString(TOKEN_KEY, null);
    }

    public void clearToken() {
        prefs.edit().remove(TOKEN_KEY).apply();
    }
}