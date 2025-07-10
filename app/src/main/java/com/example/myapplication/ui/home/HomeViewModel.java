package com.example.myapplication.ui.home;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myapplication.api.ProductService;
import com.example.myapplication.config.ApiClient;
import com.example.myapplication.dto.response.ErrorResponse;
import com.example.myapplication.model.PaginateData;
import com.example.myapplication.model.Product;
import com.example.myapplication.util.ErrorUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeViewModel extends AndroidViewModel {

    private static final String TAG = "HomeViewModel";

    private final ProductService productService;
    private final MutableLiveData<List<Product>> products = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> error = new MutableLiveData<>();

    public HomeViewModel(@NonNull Application application) {
        super(application);
        productService = ApiClient.getRetrofitInstance(application).create(ProductService.class);
        loadProducts();
    }

    public LiveData<List<Product>> getProducts() {
        return products;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getError() {
        return error;
    }

    public void loadProducts() {
        isLoading.setValue(true);
        error.setValue(null);

        productService.getAllProducts().enqueue(new Callback<PaginateData<Product>>() {
            @Override
            public void onResponse(Call<PaginateData<Product>> call, 
                                  Response<PaginateData<Product>> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    PaginateData<Product> apiResponse = response.body();
                    products.setValue(apiResponse.getItems());
                } else {
                    ErrorResponse errorResponse = ErrorUtils.processError(response);
                    error.setValue(errorResponse.getError());
                    Log.e(TAG, "Error loading products: " + errorResponse.getError());
                }
            }

            //     if (response.isSuccessful() && response.body() != null) {
            //         PaginateData<Product> apiResponse = response.body();
                    
            //         if (apiResponse.isSuccess()) {
            //             if (apiResponse.isEmpty()) {
            //                 // Handle empty response
            //                 products.setValue(new ArrayList<>());
            //             } else {
            //                 // Handle success with data
            //                 products.setValue(apiResponse.getData().getItems());
            //             }
            //         } else {
            //             // Handle error response
            //             error.setValue(apiResponse.getError().getError());
            //             Log.e(TAG, "Error loading products: " + apiResponse.getError().getError());
            //         }
            //     } else {
            //         // Handle HTTP error
            //         ErrorResponse errorResponse = ErrorUtils.processError(response);
            //         error.setValue(errorResponse.getError());
            //         Log.e(TAG, "Error loading products: " + errorResponse.getError());
            //     }
            // }

            @Override
            public void onFailure(Call<PaginateData<Product>> call, Throwable t) {
                isLoading.setValue(false);
                String errorMessage = ErrorUtils.handleThrowable(t);
                error.setValue(errorMessage);
                Log.e(TAG, "Network error loading products", t);
            }
        });
    }
}
