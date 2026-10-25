package com.example.e_commerceapplication.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a customer order in Firestore.
 * Statuses: PENDING_PAYMENT, PAID, PROCESSING, SHIPPED, COMPLETED, CANCELLED
 */
public class Order implements Serializable {
    private String orderId;
    private String orderReference;
    private String userId;
    private String customerName;
    private String customerPhone;
    private long orderDate;
    private double totalAmount;
    private double discountAmount;
    private double deliveryFee;
    private String orderStatus;  // e.g. "PENDING_PAYMENT", "PAID", "PROCESSING", "SHIPPED", "COMPLETED", "CANCELLED"
    private String paymentStatus;// e.g. "PENDING", "SUCCESS", "FAILED"
    private String deliveryType; // "DELIVERY" or "COLLECTION"
    private String deliveryAddress;
    private String mpesaReceiptNumber;
    private List<OrderItem> items = new ArrayList<>();

    public Order() {}

    public Order(String orderId, String orderReference, String userId, String customerName,
                 String customerPhone, long orderDate, double totalAmount, double discountAmount,
                 double deliveryFee, String orderStatus, String paymentStatus, String deliveryType,
                 String deliveryAddress) {
        this.orderId = orderId;
        this.orderReference = orderReference;
        this.userId = userId;
        this.customerName = customerName;
        this.customerPhone = customerPhone;
        this.orderDate = orderDate;
        this.totalAmount = totalAmount;
        this.discountAmount = discountAmount;
        this.deliveryFee = deliveryFee;
        this.orderStatus = orderStatus;
        this.paymentStatus = paymentStatus;
        this.deliveryType = deliveryType;
        this.deliveryAddress = deliveryAddress;
    }

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public String getOrderReference() { return orderReference; }
    public void setOrderReference(String orderReference) { this.orderReference = orderReference; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getCustomerPhone() { return customerPhone; }
    public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }

    public long getOrderDate() { return orderDate; }
    public void setOrderDate(long orderDate) { this.orderDate = orderDate; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public double getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(double discountAmount) { this.discountAmount = discountAmount; }

    public double getDeliveryFee() { return deliveryFee; }
    public void setDeliveryFee(double deliveryFee) { this.deliveryFee = deliveryFee; }

    public String getOrderStatus() { return orderStatus; }
    public void setOrderStatus(String orderStatus) { this.orderStatus = orderStatus; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    public String getDeliveryType() { return deliveryType; }
    public void setDeliveryType(String deliveryType) { this.deliveryType = deliveryType; }

    public String getDeliveryAddress() { return deliveryAddress; }
    public void setDeliveryAddress(String deliveryAddress) { this.deliveryAddress = deliveryAddress; }

    public String getMpesaReceiptNumber() { return mpesaReceiptNumber; }
    public void setMpesaReceiptNumber(String mpesaReceiptNumber) { this.mpesaReceiptNumber = mpesaReceiptNumber; }

    public List<OrderItem> getItems() { return items; }
    public void setItems(List<OrderItem> items) { this.items = items; }
}