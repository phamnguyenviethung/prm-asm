package com.example.myapplication.dto.request.Cart;

public class UpdateItemQuantityRequest {
    private int quantity;

    public UpdateItemQuantityRequest(int quantity) {
        this.quantity = quantity;
    }

    public UpdateItemQuantityRequest() {
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
