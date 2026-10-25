package com.example.e_commerceapplication.models;

public class InventoryTransaction {
    private String inventoryId;
    private String productId;
    private int quantityChanged;
    private String transactionType; // "DEDUCT" or "RESTOCK"
    private long transactionDate;

    public InventoryTransaction() {}

    public InventoryTransaction(String inventoryId, String productId, int quantityChanged, String transactionType, long transactionDate) {
        this.inventoryId = inventoryId;
        this.productId = productId;
        this.quantityChanged = quantityChanged;
        this.transactionType = transactionType;
        this.transactionDate = transactionDate;
    }

    public String getInventoryId() { return inventoryId; }
    public void setInventoryId(String inventoryId) { this.inventoryId = inventoryId; }

    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }

    public int getQuantityChanged() { return quantityChanged; }
    public void setQuantityChanged(int quantityChanged) { this.quantityChanged = quantityChanged; }

    public String getTransactionType() { return transactionType; }
    public void setTransactionType(String transactionType) { this.transactionType = transactionType; }

    public long getTransactionDate() { return transactionDate; }
    public void setTransactionDate(long transactionDate) { this.transactionDate = transactionDate; }
}