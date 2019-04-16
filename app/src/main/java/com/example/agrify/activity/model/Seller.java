package com.example.agrify.activity.model;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;

public class Seller {
    private String name, profilePhotoUrl, phone,email,info,productId;
    private DocumentReference StoreProductRef,SellerProductRef,AddressRef;

    private float price;
    private int stock,minQuantity,maxQuantity,imageCount;

    public Seller() {
    }

    public String getProfilePhotoUrl() {
        return profilePhotoUrl;
    }

    public void setProfilePhotoUrl(String profilePhotoUrl) {
        this.profilePhotoUrl = profilePhotoUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public DocumentReference getStoreProductRef() {
        return StoreProductRef;
    }

    public void setStoreProductRef(DocumentReference storeProductRef) {
        StoreProductRef = storeProductRef;
    }

    public DocumentReference getSellerProductRef() {
        return SellerProductRef;
    }

    public void setSellerProductRef(DocumentReference sellerProductRef) {
        SellerProductRef = sellerProductRef;
    }

    public DocumentReference getAddressRef() {
        return AddressRef;
    }

    public void setAddressRef(DocumentReference addressRef) {
        AddressRef = addressRef;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
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

    public int getImageCount() {
        return imageCount;
    }

    public void setImageCount(int imageCount) {
        this.imageCount = imageCount;
    }

    public Seller(String name, String profilePhotoUrl, String phone, String email, String info, String productId, DocumentReference storeProductRef, DocumentReference sellerProductRef, DocumentReference addressRef, float price, int stock, int minQuantity, int maxQuantity, int imageCount) {
        this.name = name;
        this.profilePhotoUrl = profilePhotoUrl;
        this.phone = phone;
        this.email = email;
        this.info = info;
        this.productId = productId;
        StoreProductRef = storeProductRef;
        SellerProductRef = sellerProductRef;
        AddressRef = addressRef;
        this.price = price;
        this.stock = stock;
        this.minQuantity = minQuantity;
        this.maxQuantity = maxQuantity;
        this.imageCount = imageCount;
    }
}
