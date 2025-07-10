package com.example.myapplication.model;

import com.google.gson.annotations.SerializedName;

public class District {
    @SerializedName("DISTRICT_ID")
    private int id;

    @SerializedName("DISTRICT_NAME")
    private String name;

    @SerializedName("DISTRICT_VALUE")
    private String code;

    @SerializedName("PROVINCE_ID")
    private int provinceId;

    public District() {
    }

    public District(int id, String name, String code, int provinceId) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.provinceId = provinceId;
    }

    // Constructor for placeholder
    public District(String id, String name, String code, String provinceId) {
        try {
            this.id = Integer.parseInt(id);
        } catch (NumberFormatException e) {
            this.id = 0;
        }
        this.name = name;
        this.code = code;
        try {
            this.provinceId = Integer.parseInt(provinceId);
        } catch (NumberFormatException e) {
            this.provinceId = 0;
        }
    }

    public String getId() {
        return String.valueOf(id);
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getProvinceId() {
        return String.valueOf(provinceId);
    }

    public void setProvinceId(int provinceId) {
        this.provinceId = provinceId;
    }

    @Override
    public String toString() {
        return name; // For spinner display
    }
}
