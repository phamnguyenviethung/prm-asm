package com.example.myapplication.ui.profile;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myapplication.api.CustomerService;
import com.example.myapplication.config.ApiClient;
import com.example.myapplication.model.Customer;
import com.example.myapplication.util.AuthManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileViewModel extends AndroidViewModel {
    private static final String TAG = "ProfileViewModel";

    private final MutableLiveData<Customer> customerData;
    private final MutableLiveData<Boolean> isLoading;
    private final MutableLiveData<String> error;
    private final CustomerService customerService;

    public ProfileViewModel(@NonNull Application application) {
        super(application);
        customerData = new MutableLiveData<>();
        isLoading = new MutableLiveData<>(false);
        error = new MutableLiveData<>();

        Context context = application.getApplicationContext();
        customerService = ApiClient.getRetrofitInstance(context).create(CustomerService.class);

        // Load customer profile when ViewModel is created
        loadCustomerProfile();
    }

    public LiveData<Customer> getCustomerData() {
        return customerData;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getError() {
        return error;
    }

    public void loadCustomerProfile() {
        isLoading.setValue(true);
        error.setValue(null);

        String token = AuthManager.getInstance(getApplication()).tokenManager.getToken();
        if (token == null || token.isEmpty()) {
            isLoading.setValue(false);
            error.setValue("User not authenticated");
            return;
        }

        customerService.getCustomerProfile().enqueue(new Callback<Customer>() {
            @Override
            public void onResponse(@NonNull Call<Customer> call, @NonNull Response<Customer> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    customerData.setValue(response.body());
                } else {
                    error.setValue("Failed to load profile: " +
                            (response.errorBody() != null ? response.code() : "Unknown error"));
                    Log.e(TAG, "Error loading profile: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Customer> call, @NonNull Throwable t) {
                isLoading.setValue(false);
                error.setValue("Network error: " + t.getMessage());
                Log.e(TAG, "Network error loading profile", t);
            }
        });
    }

    public void refreshCustomerData() {
        // Clear current data first to force refresh
        customerData.setValue(null);
        loadCustomerProfile();
    }

    public void forceRefreshCustomerData() {
        // Force refresh with a small delay to ensure server has processed the update
        new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
            customerData.setValue(null);
            loadCustomerProfile();
        }, 500); // 500ms delay
    }
}