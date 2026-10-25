package com.example.e_commerceapplication.network;

/**
 * Data Transfer Object sent from Android app to Flask backend to initiate M-Pesa STK Push.
 * Note: Amount is NOT sent from app; backend looks up order amount from Firestore securely.
 */
public class StkPushRequest {
    private String orderReference;
    private String phoneNumber;

    public StkPushRequest(String orderReference, String phoneNumber) {
        this.orderReference = orderReference;
        this.phoneNumber = phoneNumber;
    }

    public String getOrderReference() { return orderReference; }
    public void setOrderReference(String orderReference) { this.orderReference = orderReference; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
}