package com.example.myapplication.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Category {
    private String id;
    private String name;
    private String parentId;
    private String parentName;
    private String description;
    private int productCount;
    private Date createdAt;
    private Date updatedAt;
    private List<Category> children = new ArrayList<>();
    private List<Product> products = new ArrayList<>();

    public Category() {
    }

    public Category(String id, String name, String parentId, String parentName, String description, int productCount, Date createdAt, Date updatedAt, List<Category> children, List<Product> products) {
        this.id = id;
        this.name = name;
        this.parentId = parentId;
        this.parentName = parentName;
        this.description = description;
        this.productCount = productCount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.children = children;
        this.products = products;
    }

    // Getter methods
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getParentId() {
        return parentId;
    }

    public String getParentName() {
        return parentName;
    }

    public String getDescription() {
        return description;
    }

    public int getProductCount() {
        return productCount;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public List<Category> getChildren() {
        return children;
    }

    public List<Product> getProducts() {
        return products;
    }

    // Setter methods
    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setProductCount(int productCount) {
        this.productCount = productCount;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setChildren(List<Category> children) {
        this.children = children;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }
}