package com.example.myapplication.api;

import com.example.myapplication.model.Customer;

import retrofit2.Call;
import retrofit2.http.GET;

public interface CustomerService {
    @GET("customers/my-profile")
    Call<Customer> getMyProfile();
}
