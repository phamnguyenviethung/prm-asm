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

    // Getter methods
    public String getOptionId() {
        return optionId;
    }

    public String getOptionName() {
        return optionName;
    }

    public String getValueId() {
        return valueId;
    }

    public String getValue() {
        return value;
    }

    // Setter methods
    public void setOptionId(String optionId) {
        this.optionId = optionId;
    }

    public void setOptionName(String optionName) {
        this.optionName = optionName;
    }

    public void setValueId(String valueId) {
        this.valueId = valueId;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
