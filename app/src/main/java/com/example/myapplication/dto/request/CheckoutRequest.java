package com.example.myapplication.dto.request;

public class CheckoutRequest {
    private int paymentMethod;
    private String note;

    public CheckoutRequest() {
    }

    public CheckoutRequest(int paymentMethod, String note) {
        this.paymentMethod = paymentMethod;
        this.note = note;
    }

    public int getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(int paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
