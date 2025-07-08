
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

    // Getter methods
    public String getProductId() {
        return productId;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public String getVariantId() {
        return variantId;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getAvailableQuantity() {
        return availableQuantity;
    }

    public String getVariantSku() {
        return variantSku;
    }

    public int getPrice() {
        return price;
    }

    public int getFinalPrice() {
        return finalPrice;
    }

    public String getProductName() {
        return productName;
    }

    public String getValueImage() {
        return valueImage;
    }

    public String getVariantTitle() {
        return variantTitle;
    }

    public int getWeight() {
        return weight;
    }

    public String getBarCode() {
        return barCode;
    }

    // Setter methods
    public void setProductId(String productId) {
        this.productId = productId;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public void setVariantId(String variantId) {
        this.variantId = variantId;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setAvailableQuantity(int availableQuantity) {
        this.availableQuantity = availableQuantity;
    }

    public void setVariantSku(String variantSku) {
        this.variantSku = variantSku;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void setFinalPrice(int finalPrice) {
        this.finalPrice = finalPrice;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setValueImage(String valueImage) {
        this.valueImage = valueImage;
    }

    public void setVariantTitle(String variantTitle) {
        this.variantTitle = variantTitle;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }
}