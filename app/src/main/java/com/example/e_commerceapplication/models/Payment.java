package com.example.e_commerceapplication.models;

import java.io.Serializable;

/**
 * Represents an M-Pesa Payment record in Firestore.
 */
public class Payment implements Serializable {
    private String paymentId;
    private String orderId;
    private String orderReference;
    private double amount;
    private String phoneNumber;
    private String checkoutRequestId;
    private String merchantRequestId;
    private String mpesaReceiptNumber;
    private String paymentStatus; // e.g. "PENDING", "SUCCESS", "FAILED", "CANCELLED"
    private long transactionDate;
    private String resultDesc;

    public Payment() {}

    public Payment(String paymentId, String orderId, String orderReference, double amount,
                   String phoneNumber, String checkoutRequestId, String merchantRequestId,
                   String mpesaReceiptNumber, String paymentStatus, long transactionDate, String resultDesc) {
        this.paymentId = paymentId;
        this.orderId = orderId;
        this.orderReference = orderReference;
        this.amount = amount;
        this.phoneNumber = phoneNumber;
        this.checkoutRequestId = checkoutRequestId;
        this.merchantRequestId = merchantRequestId;
        this.mpesaReceiptNumber = mpesaReceiptNumber;
        this.paymentStatus = paymentStatus;
        this.transactionDate = transactionDate;
        this.resultDesc = resultDesc;
    }

    public String getPaymentId() { return paymentId; }
    public void setPaymentId(String paymentId) { this.paymentId = paymentId; }

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public String getOrderReference() { return orderReference; }
    public void setOrderReference(String orderReference) { this.orderReference = orderReference; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getCheckoutRequestId() { return checkoutRequestId; }
    public void setCheckoutRequestId(String checkoutRequestId) { this.checkoutRequestId = checkoutRequestId; }

    public String getMerchantRequestId() { return merchantRequestId; }
    public void setMerchantRequestId(String merchantRequestId) { this.merchantRequestId = merchantRequestId; }

    public String getMpesaReceiptNumber() { return mpesaReceiptNumber; }
    public void setMpesaReceiptNumber(String mpesaReceiptNumber) { this.mpesaReceiptNumber = mpesaReceiptNumber; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    public long getTransactionDate() { return transactionDate; }
    public void setTransactionDate(long transactionDate) { this.transactionDate = transactionDate; }

    public String getResultDesc() { return resultDesc; }
    public void setResultDesc(String resultDesc) { this.resultDesc = resultDesc; }
}