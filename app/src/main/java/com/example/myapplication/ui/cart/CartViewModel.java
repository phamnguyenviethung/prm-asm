package com.example.myapplication.ui.cart;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myapplication.api.CartService;
import com.example.myapplication.config.ApiClient;
import com.example.myapplication.dto.request.Cart.UpdateItemQuantityRequest;
import com.example.myapplication.dto.response.Cart.CartItem;
import com.example.myapplication.dto.response.Cart.GetCartResponse;
import com.example.myapplication.dto.response.ErrorResponse;
import com.example.myapplication.util.ErrorUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartViewModel extends AndroidViewModel {

    private static final String TAG = "CartViewModel";

    private final CartService cartService;
    private final MutableLiveData<List<CartItem>> cartItems = new MutableLiveData<>();
    private final MutableLiveData<Integer> totalQuantity = new MutableLiveData<>(0);
    private final MutableLiveData<Integer> totalPrice = new MutableLiveData<>(0);
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isEmpty = new MutableLiveData<>(true);

    public CartViewModel(@NonNull Application application) {
        super(application);
        cartService = ApiClient.getRetrofitInstance(application).create(CartService.class);
        loadCart();
    }

    public LiveData<List<CartItem>> getCartItems() {
        return cartItems;
    }

    public LiveData<Integer> getTotalQuantity() {
        return totalQuantity;
    }

    public LiveData<Integer> getTotalPrice() {
        return totalPrice;
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

    public void loadCart() {
        isLoading.setValue(true);
        error.setValue(null);

        cartService.getCart().enqueue(new Callback<GetCartResponse>() {
            @Override
            public void onResponse(Call<GetCartResponse> call, Response<GetCartResponse> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    GetCartResponse cartResponse = response.body();
                    List<CartItem> items = cartResponse.getItems();
                    
                    cartItems.setValue(items);
                    totalQuantity.setValue(cartResponse.getTotalQuantity());
                    totalPrice.setValue(cartResponse.getTotalPrice());
                    isEmpty.setValue(items == null || items.isEmpty());
                    
                    Log.d(TAG, "Cart loaded successfully with " + 
                        (items != null ? items.size() : 0) + " items");
                } else {
                    ErrorResponse errorResponse = ErrorUtils.processError(response);
                    error.setValue(errorResponse.getError());
                    Log.e(TAG, "Error loading cart: " + errorResponse.getError());
                }
            }

            @Override
            public void onFailure(Call<GetCartResponse> call, Throwable t) {
                isLoading.setValue(false);
                error.setValue("Network error: " + t.getMessage());
                Log.e(TAG, "Network error loading cart", t);
            }
        });
    }

    public void updateItemQuantity(String variantId, int newQuantity) {
        if (newQuantity <= 0) {
            removeItem(variantId);
            return;
        }

        UpdateItemQuantityRequest request = new UpdateItemQuantityRequest(newQuantity);
        
        cartService.updateItemQuantity(variantId, request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    // Reload cart to get updated data
                    loadCart();
                    Log.d(TAG, "Item quantity updated successfully");
                } else {
                    ErrorResponse errorResponse = ErrorUtils.processError(response);
                    error.setValue("Failed to update quantity: " + errorResponse.getError());
                    Log.e(TAG, "Error updating item quantity: " + errorResponse.getError());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                error.setValue("Network error: " + t.getMessage());
                Log.e(TAG, "Network error updating item quantity", t);
            }
        });
    }

    public void removeItem(String variantId) {
        cartService.removeItemFromCart(variantId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    // Reload cart to get updated data
                    loadCart();
                    Log.d(TAG, "Item removed successfully");
                } else {
                    ErrorResponse errorResponse = ErrorUtils.processError(response);
                    error.setValue("Failed to remove item: " + errorResponse.getError());
                    Log.e(TAG, "Error removing item: " + errorResponse.getError());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                error.setValue("Network error: " + t.getMessage());
                Log.e(TAG, "Network error removing item", t);
            }
        });
    }
}
