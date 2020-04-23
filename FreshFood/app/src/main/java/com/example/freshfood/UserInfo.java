package com.example.freshfood;

public class UserInfo {

    public String name;
    public String mob_num;
    public String email;
    public String community;
    public int num_orders;

    public UserInfo() {

    }

    public UserInfo(String name, String mob_num, String email, String community, int num_orders){
        this.name = name;
        this.mob_num = mob_num;
        this.email = email;
        this.community = community;
        this.num_orders = num_orders;
    }

    public String getName() {
        return name;
    }

    public String getMob_num() {
        return mob_num;
    }

    public String getEmail() {
        return email;
    }

    public String getCommunity() {
        return community;
    }

    public int getNum_orders() {
        return num_orders;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setMob_num(String mob_num) {
        this.mob_num = mob_num;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setCommunity(String community) {
        this.community = community;
    }

    public void setNum_orders(int num_orders) {
        this.num_orders = num_orders;
    }
}
