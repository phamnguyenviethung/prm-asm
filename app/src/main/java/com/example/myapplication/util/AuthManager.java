package com.example.myapplication.util;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.example.myapplication.model.Customer;
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
            HubSpotChatManager.getInstance().clearUserInfo();
        }
    }

    public void logout(Context context) {
        tokenManager.clearToken();
        // Clear HubSpot user context
        HubSpotChatManager.getInstance().clearUserInfo();
        checkLoginAndRedirect(context);
    }
}