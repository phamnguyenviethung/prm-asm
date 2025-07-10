package com.example.myapplication.api;

import com.example.myapplication.dto.response.OrderDetailResponse;
import com.example.myapplication.dto.response.OrderHistoryResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface OrderService {
    @GET("customers/orders/{id}")
    Call<OrderDetailResponse> getOrderDetail(
            @Header("Authorization") String authorization,
            @Path("id") String orderId
    );

    @GET("customers/orders")
    Call<OrderHistoryResponse> getOrderHistory(
            @Header("Authorization") String authorization,
            @Query("pageNumber") int pageNumber,
            @Query("pageSize") int pageSize
    );
}
