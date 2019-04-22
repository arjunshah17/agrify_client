package com.example.agrify.activity.order.model;

import com.google.firebase.firestore.DocumentReference;

public class OrderItem {
    private String productId, sellerId, name, productImageUrl, unit;
    private DocumentReference SellerProductRef;
    private int quantity;
    private float price, rating;
    private boolean reviewed;


    public OrderItem(String productId, String sellerId, String name, String productImageUrl, String unit, DocumentReference sellerProductRef, float price, int quantity, boolean reviewed, float rating) {
        this.productId = productId;
        this.sellerId = sellerId;
        this.name = name;
        this.productImageUrl = productImageUrl;
        this.unit = unit;
        SellerProductRef = sellerProductRef;
        this.price = price;
        this.quantity = quantity;
        this.rating = rating;
        this.reviewed = reviewed;
    }

    public OrderItem() {

    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public boolean isReviewed() {
        return reviewed;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProductImageUrl() {
        return productImageUrl;
    }

    public void setProductImageUrl(String productImageUrl) {
        this.productImageUrl = productImageUrl;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public DocumentReference getSellerProductRef() {
        return SellerProductRef;
    }

    public void setSellerProductRef(DocumentReference sellerProductRef) {
        SellerProductRef = sellerProductRef;
    }

    public void setReviewed(boolean reviewed) {
        this.reviewed = reviewed;
    }

    public int getQuantity() {
        return quantity;
    }

    public float getPrice() {
        return price;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setPrice(float price) {
        this.price = price;
    }
}
