package com.example.yuzelli.yiai.bean;

import java.io.Serializable;



public class OrderGoods implements Serializable {
    private int ordergoods_id;
    private int order_id;
    private int good_id;
    private int og_number;

    public int getOrdergoods_id() {
        return ordergoods_id;
    }
    public void setOrdergoods_id(int ordergoods_id) {
        this.ordergoods_id = ordergoods_id;
    }
    public int getOrder_id() {
        return order_id;
    }
    public void setOrder_id(int order_id) {
        this.order_id = order_id;
    }
    public int getGood_id() {
        return good_id;
    }
    public void setGood_id(int good_id) {
        this.good_id = good_id;
    }
    public int getOg_number() {
        return og_number;
    }
    public void setOg_number(int og_number) {
        this.og_number = og_number;
    }


}
