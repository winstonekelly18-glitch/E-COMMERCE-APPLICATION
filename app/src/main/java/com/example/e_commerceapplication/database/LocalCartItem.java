package com.example.e_commerceapplication.database;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Room database entity storing items added to the customer's shopping cart locally.
 * Persists cart items across app restarts.
 */
@Entity(tableName = "cart_items")
public class LocalCartItem {

    @PrimaryKey
    @NonNull
    private String productId;
    private String name;
    private double price;
    private int quantity;
    private String imageUrl;
    private int maxStock;

    public LocalCartItem(@NonNull String productId, String name, double price, int quantity, String imageUrl, int maxStock) {
        this.productId = productId;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.imageUrl = imageUrl;
        this.maxStock = maxStock;
    }

    @NonNull
    public String getProductId() { return productId; }
    public void setProductId(@NonNull String productId) { this.productId = productId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public int getMaxStock() { return maxStock; }
    public void setMaxStock(int maxStock) { this.maxStock = maxStock; }

    public double getSubtotal() {
        return price * quantity;
    }
}