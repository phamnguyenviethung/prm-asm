package com.example.myapplication.dto.request;

public class UpdateAddressRequest {
    private String streetAddress;
    private String ward;
    private String province;
    private String district;

    public UpdateAddressRequest() {
    }

    public UpdateAddressRequest(String streetAddress, String ward, String province, String district) {
        this.streetAddress = streetAddress;
        this.ward = ward;
        this.province = province;
        this.district = district;
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }

    public String getWard() {
        return ward;
    }

    public void setWard(String ward) {
        this.ward = ward;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }
}
