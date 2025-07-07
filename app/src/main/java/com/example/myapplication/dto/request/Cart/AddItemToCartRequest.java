package com.example.myapplication.dto.request.Cart;

public class AddItemToCartRequest {
    private int quantity;

    public AddItemToCartRequest(int quantity) {
        this.quantity = quantity;
    }

    public AddItemToCartRequest() {
    }
}
