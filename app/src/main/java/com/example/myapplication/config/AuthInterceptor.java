package com.example.myapplication.config;

import android.util.Log;

import com.example.myapplication.util.TokenManager;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {
    private TokenManager tokenManager;

    public AuthInterceptor(TokenManager tokenManager) {
        this.tokenManager = tokenManager;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();
        String token = tokenManager.getToken();

        if (token != null) {
            Request request = original.newBuilder()
                    .header("Authorization", "Bearer " + token)
                    .build();
            Log.d("AuthInterceptor", "Token added to request: " + token);
            return chain.proceed(request);
        }

        return chain.proceed(original);
    }
}