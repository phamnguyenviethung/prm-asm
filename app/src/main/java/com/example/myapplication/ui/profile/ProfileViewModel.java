package com.example.myapplication.ui.profile;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myapplication.api.CustomerService;
import com.example.myapplication.config.ApiClient;
import com.example.myapplication.model.Customer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileViewModel extends AndroidViewModel {

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

        customerService.getMyProfile().enqueue(new Callback<Customer>() {
            @Override
            public void onResponse(@NonNull Call<Customer> call, @NonNull Response<Customer> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    customerData.setValue(response.body());
                } else {
                    error.setValue("Failed to load profile: " +
                            (response.errorBody() != null ? response.code() : "Unknown error"));
                }
            }

            @Override
            public void onFailure(@NonNull Call<Customer> call, @NonNull Throwable t) {
                isLoading.setValue(false);
                error.setValue("Network error: " + t.getMessage());
            }
        });
    }
}