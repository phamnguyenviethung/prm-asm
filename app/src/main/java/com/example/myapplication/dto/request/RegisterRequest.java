package com.example.myapplication.dto.request;

public class RegisterRequest {
    private String email;
    private String password;
    private String fullName;
    private String phone;
    private String streetAddress;
    private String ward;
    private String province;
    private String district;


    public RegisterRequest() {
    }


    public RegisterRequest(String email, String password, String fullName, String phone, String streetAddress, String ward, String province, String district) {
        this.email = email;
        this.password = password;
        this.fullName = fullName;
        this.phone = phone;
        this.streetAddress = streetAddress;
        this.ward = ward;
        this.province = province;
        this.district = district;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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
