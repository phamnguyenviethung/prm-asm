package com.example.myapplication.config;


import android.content.Context;

import com.example.myapplication.util.TokenManager;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static Retrofit retrofit;

    public static Retrofit getRetrofitInstance(Context context) {
        if (retrofit == null) {
            TokenManager tokenManager = new TokenManager(context);

            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(new AuthInterceptor(tokenManager))
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl("http://146.190.99.154/api/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(okHttpClient)
                    .build();
        }
        return retrofit;
    }
}