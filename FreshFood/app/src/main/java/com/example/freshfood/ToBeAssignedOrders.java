package com.example.freshfood;

import java.util.ArrayList;

public class ToBeAssignedOrders {
    public String orderId;
    public String userId;
    public double pickUpLatitude;
    public double pickUpLongitude;
    public double dropLatitude;
    public double dropLongitude;
    public String userMobile;
    public String userName;
    public ArrayList<Integer> cart;
    public String status;
    public String deliveryAgentId;
    public double cost;

    public ToBeAssignedOrders() {

    }

    public ToBeAssignedOrders(String orderId, String userId, double pickUpLatitude, double pickUpLongitude, double dropLatitude, double dropLongitude, String userMobile, String userName, ArrayList<Integer> cart, String status, String deliveryAgentId, double cost) {
        this.orderId = orderId;
        this.userId = userId;
        this.pickUpLatitude = pickUpLatitude;
        this.pickUpLongitude = pickUpLongitude;
        this.dropLatitude = dropLatitude;
        this.dropLongitude = dropLongitude;
        this.userMobile = userMobile;
        this.userName = userName;
        this.cart = cart;
        this.status = status;
        this.deliveryAgentId = deliveryAgentId;
        this.cost = cost;
    }

    public double getCost() {
        return cost;
    }

    public String getStatus() {
        return status;
    }

    public String getDeliveryAgentId() {
        return deliveryAgentId;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getUserId() {
        return userId;
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

    public String getUserMobile() {
        return userMobile;
    }

    public String getUserName() {
        return userName;
    }

    public ArrayList<Integer> getCart() {
        return cart;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public void setUserMobile(String userMobile) {
        this.userMobile = userMobile;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setCart(ArrayList<Integer> cart) {
        this.cart = cart;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setDeliveryAgentId(String deliveryAgentId) {
        this.deliveryAgentId = deliveryAgentId;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }
}
