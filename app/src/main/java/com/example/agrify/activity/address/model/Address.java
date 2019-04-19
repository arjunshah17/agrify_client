package com.example.agrify.activity.address.model;

import com.google.firebase.firestore.GeoPoint;

public class Address {
    String houseNum,name;
    GeoPoint geoLocation;
    String location;
    public Address(){}

    public Address(String houseNum, String name, GeoPoint geoLocation, String location, String landMark) {
        this.houseNum = houseNum;
        this.name = name;
        this.geoLocation = geoLocation;
        this.location = location;

    }

    public String getHouseNum() {
        return houseNum;
    }

    public void setHouseNum(String houseNum) {
        this.houseNum = houseNum;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public GeoPoint getGeoLocation() {
        return geoLocation;
    }

    public void setGeoLocation(GeoPoint geoLocation) {
        this.geoLocation = geoLocation;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }


}
