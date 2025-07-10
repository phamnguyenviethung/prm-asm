package com.example.myapplication.dto.response;

import java.io.Serializable;

public class OrderItem implements Serializable {
    private String variantId;
    private String thumbnail;
    private String productName;
    private String variantTitle;
    private String sku;
    private int quantity;
    private int priceAtTime;
    private int lineDiscount;
    private int lineTotal;

    public OrderItem() {
    }

    public String getVariantId() {
        return variantId;
    }

    public void setVariantId(String variantId) {
        this.variantId = variantId;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getVariantTitle() {
        return variantTitle;
    }

    public void setVariantTitle(String variantTitle) {
        this.variantTitle = variantTitle;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getPriceAtTime() {
        return priceAtTime;
    }

    public void setPriceAtTime(int priceAtTime) {
        this.priceAtTime = priceAtTime;
    }

    public int getLineDiscount() {
        return lineDiscount;
    }

    public void setLineDiscount(int lineDiscount) {
        this.lineDiscount = lineDiscount;
    }

    public int getLineTotal() {
        return lineTotal;
    }

    public void setLineTotal(int lineTotal) {
        this.lineTotal = lineTotal;
    }
}
