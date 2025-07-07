
package com.example.myapplication.dto.response.Cart;

public class CartItem {
    private String productId;
    private String thumbnail;
    private String variantId;
    private int quantity;
    private int availableQuantity;
    private String variantSku;
    private int price;
    private int finalPrice;
    private String productName;
    private String valueImage; // Hình gắn với variant value (ví dụ: hình cho màu đỏ)
    private String variantTitle;
    private int weight;
    private String barCode;

    public CartItem(String productId, String thumbnail, String variantId, int quantity,
            int availableQuantity, String variantSku, int price, int finalPrice, String productName, String valueImage,
            String variantTitle, int weight, String barCode) {
        this.productId = productId;
        this.thumbnail = thumbnail;
        this.variantId = variantId;
        this.quantity = quantity;
        this.availableQuantity = availableQuantity;
        this.variantSku = variantSku;
        this.price = price;
        this.finalPrice = finalPrice;
        this.productName = productName;
        this.valueImage = valueImage;
        this.variantTitle = variantTitle;
        this.weight = weight;
        this.barCode = barCode;
    }

    public CartItem() {
    }
}