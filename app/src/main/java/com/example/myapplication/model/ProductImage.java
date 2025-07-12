package com.example.myapplication.model;

import java.util.Date;

public class ProductImage {
    private String id;
    private int position;
    private String src;
    private String alt;
    private String variantIds;

    public ProductImage() {
    }

    public ProductImage(String id, int position, String src, String alt, String variantIds) {
        this.id = id;
        this.position = position;
        this.src = src;
        this.alt = alt;
        this.variantIds = variantIds;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public String getAlt() {
        return alt;
    }

    public void setAlt(String alt) {
        this.alt = alt;
    }

    public String getVariantIds() {
        return variantIds;
    }

    public void setVariantIds(String variantIds) {
        this.variantIds = variantIds;
    }
}
