package com.example.myapplication.dto.response.Cart;

import java.util.ArrayList;

public class GetCartResponse {
    private String userId;
    private boolean requiresShipping;
    private ArrayList<CartItem> items;
    private int totalQuantity;
    private int totalPrice;
    public GetCartResponse(String userId, boolean requiresShipping, ArrayList<CartItem> items, int totalQuantity, int totalPrice) {
        this.userId = userId;
        this.requiresShipping = requiresShipping;
        this.items = items;
        this.totalQuantity = totalQuantity;
        this.totalPrice = totalPrice;
    }
    public GetCartResponse() {
    }
}
