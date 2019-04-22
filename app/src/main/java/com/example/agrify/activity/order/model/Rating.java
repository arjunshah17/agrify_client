package com.example.agrify.activity.order.model;

import com.google.firebase.Timestamp;

public class Rating {
    String reviewText, userId;
    Float rating;
    Timestamp timestamp;

    public Rating(String reviewText, String userId, Float rating, Timestamp timestamp) {
        this.reviewText = reviewText;
        this.userId = userId;
        this.rating = rating;
        this.timestamp = timestamp;
    }

    public Rating() {
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getReviewText() {
        return reviewText;
    }

    public void setReviewText(String reviewText) {
        this.reviewText = reviewText;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Float getRating() {
        return rating;
    }

    public void setRating(Float rating) {
        this.rating = rating;
    }
}
