package com.example.agrify.activity.model;

import java.util.HashMap;
import java.util.Map;

public class User {
    private String name, email, phone, profilePhotoUrl,cartSellerId,tempOrderSellerId;
    int cartCounter;
    boolean isAdmin;

    public User() {
    }

    public User(String name, String email, String phone, String profilePhotoUrl,String cartSellerId,int cartCounter,String tempOrderSellerId,boolean isAdmin) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.profilePhotoUrl = profilePhotoUrl;
        this.cartSellerId=cartSellerId;
        this.isAdmin=isAdmin;
        this.cartCounter=cartCounter;
        this.tempOrderSellerId=tempOrderSellerId;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public String getTempOrderSellerId() {
        return tempOrderSellerId;
    }

    public void setTempOrderSellerId(String tempOrderSellerId) {
        this.tempOrderSellerId = tempOrderSellerId;
    }

    public int getCartCounter() {
        return cartCounter;
    }

    public void setCartCounter(int cartCounter) {
        this.cartCounter = cartCounter;
    }

    public String getCartSellerId() {
        return cartSellerId;
    }

    public void setCartSellerId(String cartSellerId) {
        this.cartSellerId = cartSellerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getProfilePhotoUrl() {
        return profilePhotoUrl;
    }

    public void setProfilePhotoUrl(String profilePhotoUrl) {
        this.profilePhotoUrl = profilePhotoUrl;
    }

    public Map<String, String> toUserMap()
   {
       Map<String,String> user=new HashMap<>();
       user.put("name",name);
       user.put("phone",phone);
       return user;

   }
}
