package com.example.e_commerceapplication.models;

/**
 * Represents a registered user in Firestore.
 * Roles: "customer" or "admin".
 */
public class User {
    private String userId;
    private String fullName;
    private String email;
    private String phone;
    private String role; // "customer" or "admin"
    private String address;
    private long createdAt;

    // Default constructor required for Firebase Firestore
    public User() {}

    public User(String userId, String fullName, String email, String phone, String role, String address, long createdAt) {
        this.userId = userId;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.role = role;
        this.address = address;
        this.createdAt = createdAt;
    }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
}