package com.example.myapplication.ui.orderhistory;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myapplication.api.OrderService;
import com.example.myapplication.config.ApiClient;
import com.example.myapplication.dto.response.ErrorResponse;
import com.example.myapplication.dto.response.OrderHistoryItem;
import com.example.myapplication.dto.response.OrderHistoryResponse;
import com.example.myapplication.util.AuthManager;
import com.google.gson.Gson;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderHistoryViewModel extends AndroidViewModel {

    private static final String TAG = "OrderHistoryViewModel";

    private final OrderService orderService;

    // LiveData for UI
    private final MutableLiveData<List<OrderHistoryItem>> orderHistory = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();

    public OrderHistoryViewModel(@NonNull Application application) {
        super(application);
        orderService = ApiClient.getRetrofitInstance(application).create(OrderService.class);
    }

    public LiveData<List<OrderHistoryItem>> getOrderHistory() {
        return orderHistory;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getError() {
        return error;
    }

    public void loadOrderHistory() {
        String token = AuthManager.getInstance(getApplication()).tokenManager.getToken();
        if (token == null || token.isEmpty()) {
            error.setValue("User not authenticated");
            return;
        }

        isLoading.setValue(true);
        error.setValue(null);

        // Load first page with 20 items
        orderService.getOrderHistory("Bearer " + token, 1, 20).enqueue(new Callback<OrderHistoryResponse>() {
            @Override
            public void onResponse(Call<OrderHistoryResponse> call, Response<OrderHistoryResponse> response) {
                isLoading.setValue(false);

                if (response.isSuccessful() && response.body() != null) {
                    OrderHistoryResponse historyResponse = response.body();
                    orderHistory.setValue(historyResponse.getItems());
                    Log.d(TAG, "Order history loaded successfully: " +
                            (historyResponse.getItems() != null ? historyResponse.getItems().size() : 0) + " orders");
                } else {
                    String errorMessage = "Failed to load order history";
                    error.setValue(errorMessage);
                    Log.e(TAG, "Error loading order history: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<OrderHistoryResponse> call, Throwable t) {
                isLoading.setValue(false);
                error.setValue("Network error: " + t.getMessage());
                Log.e(TAG, "Network error loading order history", t);
            }
        });
    }

    public void refreshOrderHistory() {
        loadOrderHistory();
    }
}