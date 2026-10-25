package com.example.e_commerceapplication.utils;

/**
 * Global constants for Firestore collections, order statuses, and role types.
 */
public class Constants {

    // Firestore Collections
    public static final String COLLECTION_USERS = "users";
    public static final String COLLECTION_PRODUCTS = "products";
    public static final String COLLECTION_ORDERS = "orders";
    public static final String COLLECTION_PAYMENTS = "payments";
    public static final String COLLECTION_WISHLIST = "wishlist";
    public static final String COLLECTION_REVIEWS = "reviews";
    public static final String COLLECTION_PROMOCODES = "promocodes";
    public static final String COLLECTION_INVENTORY = "inventory_transactions";

    // Roles
    public static final String ROLE_CUSTOMER = "customer";
    public static final String ROLE_ADMIN = "admin";

    // Order Statuses
    public static final String ORDER_STATUS_PENDING_PAYMENT = "PENDING_PAYMENT";
    public static final String ORDER_STATUS_PAID = "PAID";
    public static final String ORDER_STATUS_PROCESSING = "PROCESSING";
    public static final String ORDER_STATUS_SHIPPED = "SHIPPED";
    public static final String ORDER_STATUS_COMPLETED = "COMPLETED";
    public static final String ORDER_STATUS_CANCELLED = "CANCELLED";

    // Payment Statuses
    public static final String PAYMENT_STATUS_PENDING = "PENDING";
    public static final String PAYMENT_STATUS_SUCCESS = "SUCCESS";
    public static final String PAYMENT_STATUS_FAILED = "FAILED";
    public static final String PAYMENT_STATUS_CANCELLED = "CANCELLED";
}