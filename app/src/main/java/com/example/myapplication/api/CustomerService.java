package com.example.myapplication.api;

import com.example.myapplication.dto.request.UpdateAddressRequest;
import com.example.myapplication.model.Customer;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PUT;

public interface CustomerService {

    @GET("customers/my-profile")
    Call<Customer> getCustomerProfile();

    @PUT("customers/address")
    Call<Void> updateCustomerProfile(@Header("Authorization") String token, @Body UpdateAddressRequest updateAddressRequest);
}
