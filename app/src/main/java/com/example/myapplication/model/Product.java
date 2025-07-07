package com.example.myapplication.model;

public class Product {

    private String id;

    private String name;
    private String thumbnail;
    private String handle;
    private String vendor;
    private int startingPrice;
    private int maxPrice;
    private int finalStartingPrice;
    private int finalMaxPrice;


    public Product() {
    }

    public Product(String id, String name, String thumbnail, String handle, String vendor, int startingPrice, int maxPrice, int finalStartingPrice, int finalMaxPrice) {
        this.id = id;
        this.name = name;
        this.thumbnail = thumbnail;
        this.handle = handle;
        this.vendor = vendor;
        this.startingPrice = startingPrice;
        this.maxPrice = maxPrice;
        this.finalStartingPrice = finalStartingPrice;
        this.finalMaxPrice = finalMaxPrice;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getHandle() {
        return handle;
    }

    public void setHandle(String handle) {
        this.handle = handle;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public int getStartingPrice() {
        return startingPrice;
    }

    public void setStartingPrice(int startingPrice) {
        this.startingPrice = startingPrice;
    }

    public int getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(int maxPrice) {
        this.maxPrice = maxPrice;
    }

    public int getFinalStartingPrice() {
        return finalStartingPrice;
    }

    public void setFinalStartingPrice(int finalStartingPrice) {
        this.finalStartingPrice = finalStartingPrice;
    }

    public int getFinalMaxPrice() {
        return finalMaxPrice;
    }

    public void setFinalMaxPrice(int finalMaxPrice) {
        this.finalMaxPrice = finalMaxPrice;
    }
}
