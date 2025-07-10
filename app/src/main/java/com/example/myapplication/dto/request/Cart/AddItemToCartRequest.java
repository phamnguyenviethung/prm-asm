package com.example.myapplication.dto.request.Cart;

public class AddItemToCartRequest {
    private int quantity;

    public AddItemToCartRequest(int quantity) {
        this.quantity = quantity;
    }

    public AddItemToCartRequest() {
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
