package com.example.myapplication.model;

public class VariantValue {
    private String id;
    private String value;
    private String image;
    private String variantOptionId;
    private String variantOptionName;

    public VariantValue(String id, String value, String image, String variantOptionId, String variantOptionName) {
        this.id = id;
        this.value = value;
        this.image = image;
        this.variantOptionId = variantOptionId;
        this.variantOptionName = variantOptionName;
    }

    public VariantValue() {
    }

    // Getter methods
    public String getId() {
        return id;
    }

    public String getValue() {
        return value;
    }

    public String getImage() {
        return image;
    }

    public String getVariantOptionId() {
        return variantOptionId;
    }

    public String getVariantOptionName() {
        return variantOptionName;
    }

    // Setter methods
    public void setId(String id) {
        this.id = id;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setVariantOptionId(String variantOptionId) {
        this.variantOptionId = variantOptionId;
    }

    public void setVariantOptionName(String variantOptionName) {
        this.variantOptionName = variantOptionName;
    }
}
