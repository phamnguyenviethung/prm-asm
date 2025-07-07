package com.example.myapplication.model;

public class VariantDetail {
    private String optionId;
    private String optionName;
    private String valueId;
    private String value;
    public VariantDetail(String optionId, String optionName, String valueId, String value) {
        this.optionId = optionId;
        this.optionName = optionName;
        this.valueId = valueId;
        this.value = value;
    }

    public VariantDetail() {
    }
}
