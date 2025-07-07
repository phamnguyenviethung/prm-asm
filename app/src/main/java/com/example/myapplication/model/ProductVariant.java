package com.example.myapplication.model;

import java.util.ArrayList;
import java.util.List;

public class ProductVariant {
    private String id;
    private String sku;
    private int price;
    private int finalPrice;
    private int originalPrice;
    private int weight;
    private boolean requiresShipping;
    private boolean notAllowPromotion;
    private String metadata;
    private String barcode;
    private int position;
    private String imageId;
    private String productId;
    private int totalQuantity;
    private int availableQuantity;
    private int reservedQuantity;
    private int soldQuantity;
    private List<VariantDetail> optionValues = new ArrayList<>();
    
    public ProductVariant() {
    }
    
    public ProductVariant(String id, String sku, int price, int finalPrice, int originalPrice, int weight, boolean requiresShipping, boolean notAllowPromotion, String metadata, String barcode, int position, String imageId, String productId, int totalQuantity, int availableQuantity, int reservedQuantity, int soldQuantity, List<VariantDetail> optionValues) {
        this.id = id;
        this.sku = sku;
        this.price = price;
        this.finalPrice = finalPrice;
        this.originalPrice = originalPrice;
        this.weight = weight;
        this.requiresShipping = requiresShipping;
        this.notAllowPromotion = notAllowPromotion;
        this.metadata = metadata;
        this.barcode = barcode;
        this.position = position;
        this.imageId = imageId;
        this.productId = productId;
        this.totalQuantity = totalQuantity;
        this.availableQuantity = availableQuantity;
        this.reservedQuantity = reservedQuantity;
        this.soldQuantity = soldQuantity;
        this.optionValues = optionValues;
    }
    
    // Getter methods
    public String getId() {
        return id;
    }
    
    public String getSku() {
        return sku;
    }
    
    public int getPrice() {
        return price;
    }
    
    public int getFinalPrice() {
        return finalPrice;
    }
    
    public int getOriginalPrice() {
        return originalPrice;
    }
    
    public int getWeight() {
        return weight;
    }
    
    public boolean isRequiresShipping() {
        return requiresShipping;
    }
    
    public boolean isNotAllowPromotion() {
        return notAllowPromotion;
    }
    
    public String getMetadata() {
        return metadata;
    }
    
    public String getBarcode() {
        return barcode;
    }
    
    public int getPosition() {
        return position;
    }
    
    public String getImageId() {
        return imageId;
    }
    
    public String getProductId() {
        return productId;
    }
    
    public int getTotalQuantity() {
        return totalQuantity;
    }
    
    public int getAvailableQuantity() {
        return availableQuantity;
    }
    
    public int getReservedQuantity() {
        return reservedQuantity;
    }
    
    public int getSoldQuantity() {
        return soldQuantity;
    }
    
    public List<VariantDetail> getOptionValues() {
        return optionValues;
    }
    
    // For compatibility with existing code that might use getDetails()
    public List<VariantDetail> getDetails() {
        return optionValues;
    }
    
    // Setter methods
    public void setId(String id) {
        this.id = id;
    }
    
    public void setSku(String sku) {
        this.sku = sku;
    }
    
    public void setPrice(int price) {
        this.price = price;
    }
    
    public void setFinalPrice(int finalPrice) {
        this.finalPrice = finalPrice;
    }
    
    public void setOriginalPrice(int originalPrice) {
        this.originalPrice = originalPrice;
    }
    
    public void setWeight(int weight) {
        this.weight = weight;
    }
    
    public void setRequiresShipping(boolean requiresShipping) {
        this.requiresShipping = requiresShipping;
    }
    
    public void setNotAllowPromotion(boolean notAllowPromotion) {
        this.notAllowPromotion = notAllowPromotion;
    }
    
    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }
    
    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }
    
    public void setPosition(int position) {
        this.position = position;
    }
    
    public void setImageId(String imageId) {
        this.imageId = imageId;
    }
    
    public void setProductId(String productId) {
        this.productId = productId;
    }
    
    public void setTotalQuantity(int totalQuantity) {
        this.totalQuantity = totalQuantity;
    }
    
    public void setAvailableQuantity(int availableQuantity) {
        this.availableQuantity = availableQuantity;
    }
    
    public void setReservedQuantity(int reservedQuantity) {
        this.reservedQuantity = reservedQuantity;
    }
    
    public void setSoldQuantity(int soldQuantity) {
        this.soldQuantity = soldQuantity;
    }
    
    public void setOptionValues(List<VariantDetail> optionValues) {
        this.optionValues = optionValues;
    }
    
    // Helper methods
    public boolean isAvailable() {
        return availableQuantity > 0;
    }
    
    public int getFinalStartingPrice() {
        return finalPrice;
    }
}
