package com.example.agrify.Activity.model;

public class Store {
    private String name,des;
   private  Double lowPrice;
    private int sellerCount;

    public Store(String name, String des, Double lowPrice, int sellerCount) {
        this.name = name;
        this.des = des;
        this.lowPrice = lowPrice;
        this.sellerCount = sellerCount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public Double getLowPrice() {
        return lowPrice;
    }

    public void setLowPrice(Double lowPrice) {
        this.lowPrice = lowPrice;
    }

    public int getSellerCount() {
        return sellerCount;
    }

    public void setSellerCount(int sellerCount) {
        this.sellerCount = sellerCount;
    }
}
