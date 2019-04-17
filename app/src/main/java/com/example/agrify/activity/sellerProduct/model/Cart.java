package com.example.agrify.activity.sellerProduct.model;

import com.google.firebase.firestore.DocumentReference;

public class Cart {
    String productId,sellerId;
    DocumentReference SellerProductRef;
    int Quantity;

    public Cart(String productId, String sellerId, DocumentReference sellerProductRef, int quantity) {
        this.productId = productId;
        this.sellerId = sellerId;
        SellerProductRef = sellerProductRef;
        Quantity = quantity;
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
