package com.example.myapplication.ui.map;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.maps.model.LatLng;

public class MapViewModel extends ViewModel {

    // Store location coordinates (103 Hoa Lan, Phường 7, Phú Nhuận, Hồ Chí Minh, Vietnam)
    private static final double STORE_LATITUDE = 10.7969;
    private static final double STORE_LONGITUDE = 106.6761;
    
    private final MutableLiveData<LatLng> storeLocation;
    private final MutableLiveData<String> storeName;
    private final MutableLiveData<String> storeAddress;
    private final MutableLiveData<String> storePhone;
    private final MutableLiveData<Boolean> isLoading;

    public MapViewModel() {
        storeLocation = new MutableLiveData<>();
        storeName = new MutableLiveData<>();
        storeAddress = new MutableLiveData<>();
        storePhone = new MutableLiveData<>();
        isLoading = new MutableLiveData<>();
        
        // Initialize store data
        initializeStoreData();
    }

    private void initializeStoreData() {
        storeLocation.setValue(new LatLng(STORE_LATITUDE, STORE_LONGITUDE));
        storeName.setValue("Our Store");
        storeAddress.setValue("103 Hoa Lan, Phường 7, Phú Nhuận, Hồ Chí Minh, Vietnam");
        storePhone.setValue("+84 28 3123 4567");
        isLoading.setValue(false);
    }

    public LiveData<LatLng> getStoreLocation() {
        return storeLocation;
    }

    public LiveData<String> getStoreName() {
        return storeName;
    }

    public LiveData<String> getStoreAddress() {
        return storeAddress;
    }

    public LiveData<String> getStorePhone() {
        return storePhone;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public void setLoading(boolean loading) {
        isLoading.setValue(loading);
    }
}
