package com.example.agrify.activity.order.model;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.GeoPoint;

public class Order {
    private String userHouseNum,userAddressname,orderId;
    private GeoPoint userGeoLocation;
    private String UserLocation;
    private String paymentMode;


    private String userId;
    private String SellerId;
   private Timestamp timestamp;
    private String orderStatus;

    public Order() {
    }

    public Order(String userHouseNum, String userAddressname, String orderId, GeoPoint userGeoLocation, String userLocation, String paymentMode, String userId, String sellerId, Timestamp timestamp, String orderStatus) {
        this.userHouseNum = userHouseNum;
        this.userAddressname = userAddressname;
        this.orderId = orderId;
        this.userGeoLocation = userGeoLocation;
        UserLocation = userLocation;
        this.paymentMode = paymentMode;
        this.userId = userId;
        SellerId = sellerId;
        this.timestamp = timestamp;
        this.orderStatus = orderStatus;

    }

    public String getUserHouseNum() {
        return userHouseNum;
    }

    public void setUserHouseNum(String userHouseNum) {
        this.userHouseNum = userHouseNum;
    }

    public String getUserAddressname() {
        return userAddressname;
    }

    public void setUserAddressname(String userAddressname) {
        this.userAddressname = userAddressname;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public GeoPoint getUserGeoLocation() {
        return userGeoLocation;
    }

    public void setUserGeoLocation(GeoPoint userGeoLocation) {
        this.userGeoLocation = userGeoLocation;
    }

    public String getUserLocation() {
        return UserLocation;
    }

    public void setUserLocation(String userLocation) {
        UserLocation = userLocation;
    }

    public String getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(String paymentMode) {
        this.paymentMode = paymentMode;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSellerId() {
        return SellerId;
    }

    public void setSellerId(String sellerId) {
        SellerId = sellerId;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }
}
