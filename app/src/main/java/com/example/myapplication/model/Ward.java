package com.example.myapplication.model;

import com.google.gson.annotations.SerializedName;

public class Ward {
    @SerializedName("WARDS_ID")
    private int id;

    @SerializedName("WARDS_NAME")
    private String name;

    @SerializedName("DISTRICT_ID")
    private int districtId;

    public Ward() {
    }

    public Ward(int id, String name, int districtId) {
        this.id = id;
        this.name = name;
        this.districtId = districtId;
    }

    // Constructor for placeholder
    public Ward(String id, String name, String code, String districtId) {
        try {
            this.id = Integer.parseInt(id);
        } catch (NumberFormatException e) {
            this.id = 0;
        }
        this.name = name;
        try {
            this.districtId = Integer.parseInt(districtId);
        } catch (NumberFormatException e) {
            this.districtId = 0;
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

    public String getDistrictId() {
        return String.valueOf(districtId);
    }

    public void setDistrictId(int districtId) {
        this.districtId = districtId;
    }

    @Override
    public String toString() {
        return name; // For spinner display
    }
}
