package com.example.myapplication.model;

import java.util.ArrayList;
import java.util.List;

public class ProductVariant {
    private String id;
    private String name;
    private String description;
    private String promotionDescription;
    private Integer soldQuantity;
    private String categoryId;
    private String categoryName;
    private String status;
    private String thumbnail;
    private String vendor;
    private String handle;
    private boolean available;
    private boolean notAllowPromotion;
    private List<ProductImage> images = new ArrayList<>();
    private List<ProductVariant> variants = new ArrayList<>();
    private List<VariantOption> options = new ArrayList<>();
    private int startingPrice;
    private int maxPrice;
    private int promotionSale;
    private int totalQuantity;
    private int reservedQuantity;
    private int availableQuantity;
    
    // Calculated properties
    public int getFinalStartingPrice() {
        return startingPrice - promotionSale;
    }
    
    public int getFinalMaxPrice() {
        return maxPrice - promotionSale;
    }

    public ProductVariant() {
    }

    // Getter methods
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getPromotionDescription() {
        return promotionDescription;
    }

    public Integer getSoldQuantity() {
        return soldQuantity;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public String getStatus() {
        return status;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public String getVendor() {
        return vendor;
    }

    public String getHandle() {
        return handle;
    }

    public boolean isAvailable() {
        return available;
    }

    public boolean isNotAllowPromotion() {
        return notAllowPromotion;
    }

    public List<ProductImage> getImages() {
        return images;
    }

    public List<ProductVariant> getVariants() {
        return variants;
    }

    public List<VariantOption> getOptions() {
        return options;
    }

    public int getStartingPrice() {
        return startingPrice;
    }

    public int getMaxPrice() {
        return maxPrice;
    }

    public int getPromotionSale() {
        return promotionSale;
    }

    public int getTotalQuantity() {
        return totalQuantity;
    }

    public int getReservedQuantity() {
        return reservedQuantity;
    }

    public int getAvailableQuantity() {
        return availableQuantity;
    }

    public ProductVariant(String id, String name, String description, String promotionDescription, Integer soldQuantity, String categoryId, String categoryName, String status, String thumbnail, String vendor, String handle, boolean available, boolean notAllowPromotion, List<ProductImage> images, List<ProductVariant> variants, List<VariantOption> options, int startingPrice, int maxPrice, int promotionSale, int totalQuantity, int reservedQuantity, int availableQuantity) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.promotionDescription = promotionDescription;
        this.soldQuantity = soldQuantity;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.status = status;
        this.thumbnail = thumbnail;
        this.vendor = vendor;
        this.handle = handle;
        this.available = available;
        this.notAllowPromotion = notAllowPromotion;
        this.images = images;
        this.variants = variants;
        this.options = options;
        this.startingPrice = startingPrice;
        this.maxPrice = maxPrice;
        this.promotionSale = promotionSale;
        this.totalQuantity = totalQuantity;
        this.reservedQuantity = reservedQuantity;
        this.availableQuantity = availableQuantity;
    }
}
