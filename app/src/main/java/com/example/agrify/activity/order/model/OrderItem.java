package com.example.agrify.activity.order.model;

import com.google.firebase.firestore.DocumentReference;

public class OrderItem {
    String productId, sellerId, name, productImageUrl, unit;
    DocumentReference SellerProductRef;
    int Quantity;
    private float price;

    public OrderItem(String productId, String sellerId, String name, String productImageUrl, String unit, DocumentReference sellerProductRef, float price, int quantity) {
        this.productId = productId;
        this.sellerId = sellerId;
        this.name = name;
        this.productImageUrl = productImageUrl;
        this.unit = unit;
        SellerProductRef = sellerProductRef;
        this.price = price;
        Quantity = quantity;
    }

    public OrderItem() {

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

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public int getQuantity() {
        return Quantity;
    }

    public void setQuantity(int quantity) {
        Quantity = quantity;
    }
}
