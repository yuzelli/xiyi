package com.example.yuzelli.yiai.bean;

import java.io.Serializable;



public class Goods implements Serializable{
    private int good_id;
    private int business_id;
    private String g_name;
    private int g_price;
    public int getGood_id() {
        return good_id;
    }
    public void setGood_id(int good_id) {
        this.good_id = good_id;
    }
    public int getBusiness_id() {
        return business_id;
    }
    public void setBusiness_id(int business_id) {
        this.business_id = business_id;
    }
    public String getG_name() {
        return g_name;
    }
    public void setG_name(String g_name) {
        this.g_name = g_name;
    }
    public int getG_price() {
        return g_price;
    }
    public void setG_price(int g_price) {
        this.g_price = g_price;
    }


}
