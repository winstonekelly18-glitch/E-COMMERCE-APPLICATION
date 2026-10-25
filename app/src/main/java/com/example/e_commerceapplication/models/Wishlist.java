package com.example.e_commerceapplication.models;

public class Wishlist {
    private String wishlistId;
    private String userId;
    private String productId;
    private long addedAt;

    public Wishlist() {}

    public Wishlist(String wishlistId, String userId, String productId, long addedAt) {
        this.wishlistId = wishlistId;
        this.userId = userId;
        this.productId = productId;
        this.addedAt = addedAt;
    }

    public String getWishlistId() { return wishlistId; }
    public void setWishlistId(String wishlistId) { this.wishlistId = wishlistId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }

    public long getAddedAt() { return addedAt; }
    public void setAddedAt(long addedAt) { this.addedAt = addedAt; }
}