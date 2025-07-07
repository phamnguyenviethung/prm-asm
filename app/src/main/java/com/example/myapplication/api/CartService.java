package com.example.myapplication.api;

import com.example.myapplication.dto.response.Cart.GetCartResponse;
import com.example.myapplication.dto.request.Cart.AddItemToCartRequest;
import com.example.myapplication.dto.request.Cart.UpdateItemQuantityRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface CartService {
    @GET("carts")
    Call<GetCartResponse> getCart();

    @POST("carts/items/{variantId}")
    Call<Void> addItemToCart(@Path("variantId") String variantId, @Body AddItemToCartRequest request);

    @PATCH("carts/items/{variantId}")
    Call<Void> updateItemQuantity(@Path("variantId") String variantId, @Body UpdateItemQuantityRequest request);

    @DELETE("carts/items/{variantId}")
    Call<Void> removeItemFromCart(@Path("variantId") String variantId);
}
