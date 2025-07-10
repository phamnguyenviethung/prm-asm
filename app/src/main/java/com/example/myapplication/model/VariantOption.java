package com.example.myapplication.model;

import java.util.ArrayList;
import java.util.List;

public class VariantOption {
    private String id;
    private String name;
    private int position;
    private List<VariantValue> values = new ArrayList<>();

    public VariantOption(String id, String name, int position, List<VariantValue> values) {
        this.id = id;
        this.name = name;
        this.position = position;
        this.values = values;
    }
    
    public VariantOption() {
    }

    // Getter methods
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getPosition() {
        return position;
    }

    public List<VariantValue> getValues() {
        return values;
    }

    // Setter methods
    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public void setValues(List<VariantValue> values) {
        this.values = values;
    }
}
