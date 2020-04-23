package com.example.freshfood;

import java.io.Serializable;
import java.util.ArrayList;

public class Orders implements Serializable {

    public double pickUpLatitude;
    public double pickUpLongitude;
    public double dropLatitude;
    public double dropLongitude;
    //int[] cart = new int[4];
    public ArrayList<Integer> cart;
    public String timeStamp;
    public String status;

    public Orders(){

    }

    public Orders(double pickUpLatitude, double pickUpLongitude, double dropLatitude, double dropLongitude, ArrayList<Integer> cart, String timestamp, String status){
        this.pickUpLatitude = pickUpLatitude;
        this.pickUpLongitude = pickUpLongitude;
        this.dropLatitude = dropLatitude;
        this.dropLongitude = dropLongitude;
        this.cart = cart;
        this.timeStamp = timestamp;
        this.status = status;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public double getPickUpLatitude() {
        return pickUpLatitude;
    }

    public double getPickUpLongitude() {
        return pickUpLongitude;
    }

    public double getDropLatitude() {
        return dropLatitude;
    }

    public double getDropLongitude() {
        return dropLongitude;
    }

    public ArrayList<Integer> getCart() {
        return cart;
    }

    public String getStatus() {
        return status;
    }

    public void setPickUpLatitude(double pickUpLatitude) {
        this.pickUpLatitude = pickUpLatitude;
    }

    public void setPickUpLongitude(double pickUpLongitude) {
        this.pickUpLongitude = pickUpLongitude;
    }

    public void setDropLatitude(double dropLatitude) {
        this.dropLatitude = dropLatitude;
    }

    public void setDropLongitude(double dropLongitude) {
        this.dropLongitude = dropLongitude;
    }

    public void setCart(ArrayList<Integer> cart) {
        this.cart = cart;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
