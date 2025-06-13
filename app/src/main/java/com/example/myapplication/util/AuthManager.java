package com.example.myapplication.util;

import android.content.Context;
import android.content.Intent;

import com.example.myapplication.ui.auth.LoginActivity;

public class AuthManager {
    private static AuthManager instance;
    private final TokenManager tokenManager;

    private AuthManager(Context context) {
        tokenManager = new TokenManager(context);
    }

    public static synchronized AuthManager getInstance(Context context) {
        if (instance == null) {
            instance = new AuthManager(context.getApplicationContext());
        }
        return instance;
    }

    public boolean isLoggedIn() {
        return tokenManager.getToken() != null && !tokenManager.getToken().isEmpty();
    }

    public void checkLoginAndRedirect(Context context) {
        if (!isLoggedIn()) {
            Intent intent = new Intent(context, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(intent);
        }
    }

    public void logout(Context context) {
        tokenManager.clearToken();
        checkLoginAndRedirect(context);
    }
}