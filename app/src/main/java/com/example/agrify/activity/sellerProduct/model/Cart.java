package com.example.agrify.activity.sellerProduct.model;

import com.google.firebase.firestore.DocumentReference;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Cart {
    private DocumentReference sellerCartProductRef, userCartProductRef, SellerProductRef;
    private String productId, sellerId, name, productImageUrl, unit;
    private float price;

    private int quantity, minQuantity, maxQuantity;


    public Cart(String productId, String sellerId, String name, String productImageUrl, String unit, DocumentReference sellerCartProductRef, DocumentReference userCartProductRef, DocumentReference sellerProductRef, float price, int quantity, int minQuantity, int maxQuantity) {
        this.productId = productId;
        this.sellerId = sellerId;
        this.name = name;
        this.productImageUrl = productImageUrl;
        this.unit = unit;
        this.sellerCartProductRef = sellerCartProductRef;
        this.userCartProductRef = userCartProductRef;
        SellerProductRef = sellerProductRef;
        this.price = price;
        this.quantity = quantity;
        this.minQuantity = minQuantity;
        this.maxQuantity = maxQuantity;
    }

    public Cart() {

    }

    public DocumentReference getSellerCartProductRef() {
        return sellerCartProductRef;
    }

    public void setSellerCartProductRef(DocumentReference sellerCartProductRef) {
        this.sellerCartProductRef = sellerCartProductRef;
    }

    public DocumentReference getUserCartProductRef() {
        return userCartProductRef;
    }

    public void setUserCartProductRef(DocumentReference userCartProductRef) {
        this.userCartProductRef = userCartProductRef;
    }

    public DocumentReference getSellerProductRef() {
        return SellerProductRef;
    }

    public void setSellerProductRef(DocumentReference sellerProductRef) {
        SellerProductRef = sellerProductRef;
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

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
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
}
