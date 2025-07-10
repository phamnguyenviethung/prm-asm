package com.example.myapplication.api;

import com.example.myapplication.dto.request.LoginRequest;
import com.example.myapplication.dto.request.RegisterRequest;
import com.example.myapplication.dto.response.LoginResponse;
import com.example.myapplication.dto.response.RegisterResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthService {

    @POST("auth/customer-login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);

    @POST("auth/customer-register")
    Call<RegisterResponse> register(@Body RegisterRequest registerRequest);

}
