package com.example.agrify.activity.order.model;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.GeoPoint;

public class Order {
    private String userHouseNum,userAddressname,orderId;
    private GeoPoint userGeoLocation;
    private String UserLocation;
    private String sellerHouseNum,sellerAddressname;
    private GeoPoint sellerGeoLocation;
    private String sellerLocation;
    private String paymentMode;
    private String userName, userEmail, userPhone, UserProfilePhotoUrl;
    private String Sellername, sellerEmail, sellerPhone, sellerProfilePhotoUrl;
    private String userId;
    private String SellerId;
   private Timestamp timestamp;
    private String orderStatus;

    public Order() {
    }

    public Order(String userHouseNum, String userAddressname, String orderId, GeoPoint userGeoLocation, String userLocation, String sellerHouseNum, String sellerAddressname, GeoPoint sellerGeoLocation, String sellerLocation, String paymentMode, String userName, String userEmail, String userPhone, String userProfilePhotoUrl, String sellername, String sellerEmail, String sellerPhone, String sellerProfilePhotoUrl, String userId, String sellerId, Timestamp timestamp, String orderStatus) {
        this.userHouseNum = userHouseNum;
        this.userAddressname = userAddressname;
        this.orderId = orderId;
        this.userGeoLocation = userGeoLocation;
        UserLocation = userLocation;
        this.sellerHouseNum = sellerHouseNum;
        this.sellerAddressname = sellerAddressname;
        this.sellerGeoLocation = sellerGeoLocation;
        this.sellerLocation = sellerLocation;
        this.paymentMode = paymentMode;
        this.userName = userName;
        this.userEmail = userEmail;
        this.userPhone = userPhone;
        UserProfilePhotoUrl = userProfilePhotoUrl;
        Sellername = sellername;
        this.sellerEmail = sellerEmail;
        this.sellerPhone = sellerPhone;
        this.sellerProfilePhotoUrl = sellerProfilePhotoUrl;
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

    public String getSellerHouseNum() {
        return sellerHouseNum;
    }

    public void setSellerHouseNum(String sellerHouseNum) {
        this.sellerHouseNum = sellerHouseNum;
    }

    public String getSellerAddressname() {
        return sellerAddressname;
    }

    public void setSellerAddressname(String sellerAddressname) {
        this.sellerAddressname = sellerAddressname;
    }

    public GeoPoint getSellerGeoLocation() {
        return sellerGeoLocation;
    }

    public void setSellerGeoLocation(GeoPoint sellerGeoLocation) {
        this.sellerGeoLocation = sellerGeoLocation;
    }

    public String getSellerLocation() {
        return sellerLocation;
    }

    public void setSellerLocation(String sellerLocation) {
        this.sellerLocation = sellerLocation;
    }

    public String getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(String paymentMode) {
        this.paymentMode = paymentMode;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getUserProfilePhotoUrl() {
        return UserProfilePhotoUrl;
    }

    public void setUserProfilePhotoUrl(String userProfilePhotoUrl) {
        UserProfilePhotoUrl = userProfilePhotoUrl;
    }

    public String getSellername() {
        return Sellername;
    }

    public void setSellername(String sellername) {
        Sellername = sellername;
    }

    public String getSellerEmail() {
        return sellerEmail;
    }

    public void setSellerEmail(String sellerEmail) {
        this.sellerEmail = sellerEmail;
    }

    public String getSellerPhone() {
        return sellerPhone;
    }

    public void setSellerPhone(String sellerPhone) {
        this.sellerPhone = sellerPhone;
    }

    public String getSellerProfilePhotoUrl() {
        return sellerProfilePhotoUrl;
    }

    public void setSellerProfilePhotoUrl(String sellerProfilePhotoUrl) {
        this.sellerProfilePhotoUrl = sellerProfilePhotoUrl;
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
