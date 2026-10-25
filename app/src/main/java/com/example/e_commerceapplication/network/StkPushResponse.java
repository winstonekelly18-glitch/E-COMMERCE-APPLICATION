package com.example.e_commerceapplication.network;

/**
 * Response from Flask backend after submitting STK Push request to Safaricom Daraja API.
 */
public class StkPushResponse {
    private boolean success;
    private String message;
    private String checkoutRequestId;
    private String merchantRequestId;
    private String orderReference;

    public StkPushResponse() {}

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getCheckoutRequestId() { return checkoutRequestId; }
    public void setCheckoutRequestId(String checkoutRequestId) { this.checkoutRequestId = checkoutRequestId; }

    public String getMerchantRequestId() { return merchantRequestId; }
    public void setMerchantRequestId(String merchantRequestId) { this.merchantRequestId = merchantRequestId; }

    public String getOrderReference() { return orderReference; }
    public void setOrderReference(String orderReference) { this.orderReference = orderReference; }
}