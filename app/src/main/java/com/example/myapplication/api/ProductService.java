package com.example.myapplication.api;

import com.example.myapplication.model.PaginateData;
import com.example.myapplication.model.Product;
import com.example.myapplication.model.ProductDetail;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ProductService {
    @GET("products")
    Call<PaginateData<Product>> getAllProducts();

    @GET("products")
    Call<PaginateData<Product>> getProducts(
            @Query("page") int page,
            @Query("pageSize") int pageSize);
    @GET("products/{id}")
    Call<ProductDetail> getProductDetail(@Path("id") String id);
}

