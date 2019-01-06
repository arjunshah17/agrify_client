package com.example.agrify.activity.model;

public class User {
String name,userProfilePhoto;

    public User(String name, String userProfilePhoto) {
        this.name = name;
        this.userProfilePhoto = userProfilePhoto;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserProfilePhoto() {
        return userProfilePhoto;
    }

    public void setUserProfilePhoto(String userProfilePhoto) {
        this.userProfilePhoto = userProfilePhoto;
    }
}
