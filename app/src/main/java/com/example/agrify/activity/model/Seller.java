package com.example.agrify.activity.model;

public class Seller {
    String name,profilePhotoUrl,price,phone;

Seller(){}

    public Seller(String name, String profilePhotoUrl, String price, String phone) {
        this.name = name;
        this.profilePhotoUrl = profilePhotoUrl;
        this.price = price;
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfilePhotoUrl() {
        return profilePhotoUrl;
    }

    public void setProfilePhotoUrl(String profilePhotoUrl) {
        this.profilePhotoUrl = profilePhotoUrl;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
