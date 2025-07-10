package com.example.myapplication.api;

import com.example.myapplication.model.District;
import com.example.myapplication.model.Province;
import com.example.myapplication.model.Ward;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RegionService {
    
    @GET("regions/provinces")
    Call<List<Province>> getProvinces();
    
    @GET("regions/districts")
    Call<List<District>> getDistricts(@Query("provinceId") String provinceId);
    
    @GET("regions/wards")
    Call<List<Ward>> getWards(@Query("districtId") String districtId);
}
