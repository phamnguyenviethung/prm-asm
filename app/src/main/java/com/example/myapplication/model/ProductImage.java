package com.example.myapplication.model;

import java.util.Date;

public class ProductImage {
    private String id;
    private int position;
    private String Src;
    private String Alt;
    private String VariantIds;

    public ProductImage() {
    }

    public ProductImage(String id, int position, String src, String alt, String variantIds) {
        this.id = id;
        this.position = position;
        Src = src;
        Alt = alt;
        VariantIds = variantIds;
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
        return Src;
    }

    public void setSrc(String src) {
        Src = src;
    }

    public String getAlt() {
        return Alt;
    }

    public void setAlt(String alt) {
        Alt = alt;
    }

    public String getVariantIds() {
        return VariantIds;
    }

    public void setVariantIds(String variantIds) {
        VariantIds = variantIds;
    }
}
