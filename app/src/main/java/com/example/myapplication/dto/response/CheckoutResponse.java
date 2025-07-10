package com.example.myapplication.dto.response;

public class CheckoutResponse {
    private String orderId; // Order ID returned from API

    public CheckoutResponse() {
    }

    public CheckoutResponse(String id) {
        this.orderId = id;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
}
