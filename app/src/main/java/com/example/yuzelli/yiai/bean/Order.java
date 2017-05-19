package com.example.yuzelli.yiai.bean;

import java.io.Serializable;



public class Order implements Serializable {
    private int order_id;
    private int user_id;
    private int business_id;
    private int o_pay;
    private String o_remarks;
    private String o_creattime;

    public String getO_creattime() {
        return o_creattime;
    }
    public void setO_creattime(String o_creattime) {
        this.o_creattime = o_creattime;
    }
    public int getO_pay() {
        return o_pay;
    }
    public void setO_pay(int o_pay) {
        this.o_pay = o_pay;
    }
    public int getOrder_id() {
        return order_id;
    }
    public void setOrder_id(int order_id) {
        this.order_id = order_id;
    }

    public int getUser_id() {
        return user_id;
    }
    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }
    public int getBusiness_id() {
        return business_id;
    }
    public void setBusiness_id(int business_id) {
        this.business_id = business_id;
    }
    public String getO_remarks() {
        return o_remarks;
    }
    public void setO_remarks(String o_remarks) {
        this.o_remarks = o_remarks;
    }


}
