package com.example.myapplication.dto.response;

import com.google.gson.annotations.SerializedName;

public class ErrorResponse {
    @SerializedName("error")
    private String error;

    @SerializedName("isSuccess")
    private boolean isSuccess;

    @SerializedName("isFailure")
    private boolean isFailure;
    
    public ErrorResponse(String error,  boolean isSuccess, boolean isFailure) {
        this.error = error;
        this.isSuccess = isSuccess;
        this.isFailure = isFailure;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    public boolean isFailure() {
        return isFailure;
    }

    public void setFailure(boolean failure) {
        isFailure = failure;
    }
}
