package com.example.myapplication.model;

import com.google.gson.annotations.SerializedName;

public class Province {
    @SerializedName("PROVINCE_ID")
    private int id;

    @SerializedName("PROVINCE_NAME")
    private String name;

    @SerializedName("PROVINCE_CODE")
    private String code;

    public Province() {
    }

    public Province(int id, String name, String code) {
        this.id = id;
        this.name = name;
        this.code = code;
    }

    // Constructor for placeholder
    public Province(String id, String name, String code) {
        try {
            this.id = Integer.parseInt(id);
        } catch (NumberFormatException e) {
            this.id = 0;
        }
        this.name = name;
        this.code = code;
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

    @Override
    public String toString() {
        return name; // For spinner display
    }
}
