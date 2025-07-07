package com.example.myapplication.ui.productdetail;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myapplication.api.ProductService;
import com.example.myapplication.config.ApiClient;
import com.example.myapplication.dto.response.ErrorResponse;
import com.example.myapplication.model.ProductDetail;
import com.example.myapplication.util.ErrorUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductDetailViewModel extends AndroidViewModel {

    private static final String TAG = "ProductDetailViewModel";

    private final ProductService productService;
    private final MutableLiveData<ProductDetail> productDetail = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isEmpty = new MutableLiveData<>(false);

    public ProductDetailViewModel(@NonNull Application application) {
        super(application);
        productService = ApiClient.getRetrofitInstance(application).create(ProductService.class);
    }

    public LiveData<ProductDetail> getProductDetail() {
        return productDetail;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getError() {
        return error;
    }
    
    public LiveData<Boolean> getIsEmpty() {
        return isEmpty;
    }

    public void loadProductDetail(String productId) {
        isLoading.setValue(true);
        error.setValue(null);
        isEmpty.setValue(false);

        productService.getProductDetail(productId).enqueue(new Callback<ProductDetail>() {
            @Override
            public void onResponse(Call<ProductDetail> call, Response<ProductDetail> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    productDetail.setValue(response.body());
                } else {
                    ErrorResponse errorResponse = ErrorUtils.processError(response);
                    error.setValue(errorResponse.getError());
                    Log.e(TAG, "Error loading product detail: " + errorResponse.getError());
                }
            }

            @Override
            public void onFailure(Call<ProductDetail> call, Throwable t) {
                isLoading.setValue(false);
                String errorMessage = ErrorUtils.handleThrowable(t);
                error.setValue(errorMessage);
                Log.e(TAG, "Network error loading product detail", t);
            }
        });
    }
    
    // Add to cart functionality
    public void addToCart(String productId, String variantId, int quantity) {
        // Implement add to cart API call
        // This would typically call a CartService method
        Log.d(TAG, "Adding to cart: productId=" + productId + ", variantId=" + variantId + ", quantity=" + quantity);
    }
}