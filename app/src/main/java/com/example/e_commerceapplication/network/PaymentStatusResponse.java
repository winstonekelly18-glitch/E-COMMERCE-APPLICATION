package com.example.e_commerceapplication.network;

/**
 * Response from Flask backend when polling payment status for a specific order.
 */
public class PaymentStatusResponse {
    private boolean success;
    private String orderReference;
    private String paymentStatus; // "PENDING", "SUCCESS", "FAILED", "CANCELLED"
    private String orderStatus;
    private String mpesaReceiptNumber;
    private double amount;
    private String resultDesc;

    public PaymentStatusResponse() {}

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getOrderReference() { return orderReference; }
    public void setOrderReference(String orderReference) { this.orderReference = orderReference; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    public String getOrderStatus() { return orderStatus; }
    public void setOrderStatus(String orderStatus) { this.orderStatus = orderStatus; }

    public String getMpesaReceiptNumber() { return mpesaReceiptNumber; }
    public void setMpesaReceiptNumber(String mpesaReceiptNumber) { this.mpesaReceiptNumber = mpesaReceiptNumber; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getResultDesc() { return resultDesc; }
    public void setResultDesc(String resultDesc) { this.resultDesc = resultDesc; }
}