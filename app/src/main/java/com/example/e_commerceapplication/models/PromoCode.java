package com.example.e_commerceapplication.models;

public class PromoCode {
    private String code;
    private double discountPercentage;
    private double maxDiscount;
    private boolean active;

    public PromoCode() {}

    public PromoCode(String code, double discountPercentage, double maxDiscount, boolean active) {
        this.code = code;
        this.discountPercentage = discountPercentage;
        this.maxDiscount = maxDiscount;
        this.active = active;
    }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public double getDiscountPercentage() { return discountPercentage; }
    public void setDiscountPercentage(double discountPercentage) { this.discountPercentage = discountPercentage; }

    public double getMaxDiscount() { return maxDiscount; }
    public void setMaxDiscount(double maxDiscount) { this.maxDiscount = maxDiscount; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}