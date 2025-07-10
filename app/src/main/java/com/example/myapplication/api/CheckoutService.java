package com.example.myapplication.api;

import com.example.myapplication.dto.request.CheckoutRequest;
import com.example.myapplication.dto.response.CheckoutResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface CheckoutService {
    @POST("checkout")
    Call<CheckoutResponse> checkout(
            @Header("Authorization") String authorization,
            @Body CheckoutRequest request
    );
}
