package com.example.myapplication.api;

import com.example.myapplication.dto.LoginRequest;
import com.example.myapplication.dto.LoginResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthService {

    @POST("auth/customer-login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);

}
