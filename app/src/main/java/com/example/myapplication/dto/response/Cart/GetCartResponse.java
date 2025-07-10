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

    // Getter methods
    public String getUserId() {
        return userId;
    }

    public boolean isRequiresShipping() {
        return requiresShipping;
    }

    public ArrayList<CartItem> getItems() {
        return items;
    }

    public int getTotalQuantity() {
        return totalQuantity;
    }

    public int getTotalPrice() {
        return totalPrice;
    }

    // Setter methods
    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setRequiresShipping(boolean requiresShipping) {
        this.requiresShipping = requiresShipping;
    }

    public void setItems(ArrayList<CartItem> items) {
        this.items = items;
    }

    public void setTotalQuantity(int totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public void setTotalPrice(int totalPrice) {
        this.totalPrice = totalPrice;
    }
}
