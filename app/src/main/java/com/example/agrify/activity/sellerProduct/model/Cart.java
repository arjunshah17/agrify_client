package com.example.agrify.activity.sellerProduct.model;

import com.google.firebase.firestore.DocumentReference;

public class Cart {
    String productId,sellerId,name, productImageUrl,unit;
    DocumentReference SellerProductRef;
       private float price;

    int Quantity,minQuantity,maxQuantity;

    public Cart(String productId, String sellerId, String name, String productImageUrl, String unit, DocumentReference sellerProductRef, float price, int quantity, int minQuantity, int maxQuantity) {
        this.productId = productId;
        this.sellerId = sellerId;
        this.name = name;
        this.productImageUrl = productImageUrl;
        this.unit = unit;
        SellerProductRef = sellerProductRef;
        this.price = price;
        Quantity = quantity;
        this.minQuantity = minQuantity;
        this.maxQuantity = maxQuantity;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public int getMinQuantity() {
        return minQuantity;
    }

    public void setMinQuantity(int minQuantity) {
        this.minQuantity = minQuantity;
    }

    public int getMaxQuantity() {
        return maxQuantity;
    }

    public void setMaxQuantity(int maxQuantity) {
        this.maxQuantity = maxQuantity;
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

    public Cart() {

    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public DocumentReference getSellerProductRef() {
        return SellerProductRef;
    }

    public void setSellerProductRef(DocumentReference sellerProductRef) {
        SellerProductRef = sellerProductRef;
    }

    public int getQuantity() {
        return Quantity;
    }

    public void setQuantity(int quantity) {
        Quantity = quantity;
    }
}
