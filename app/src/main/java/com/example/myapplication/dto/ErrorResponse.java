package com.example.myapplication.dto;

import com.google.gson.annotations.SerializedName;

public class ErrorResponse {

    @SerializedName("error")
    private String error;

    @SerializedName("title")
    private String title;


    public ErrorResponse(String error) {
        this.error = error;

    }

    public ErrorResponse(String error, String title) {
        this.error = error;
        this.title = title;
    }

    public String getError() {

        if (this.error == null || this.error.isEmpty()) {
            return this.title;
        }

        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
