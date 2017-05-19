package com.example.yuzelli.yiai.bean;

import java.io.Serializable;

public class TimeBean implements Serializable {
    private int time_id;
    private int order_id;
    private String t_content;
    public int getTime_id() {
        return time_id;
    }
    public void setTime_id(int time_id) {
        this.time_id = time_id;
    }
    public int getOrder_id() {
        return order_id;
    }
    public void setOrder_id(int order_id) {
        this.order_id = order_id;
    }
    public String getT_content() {
        return t_content;
    }
    public void setT_content(String t_content) {
        this.t_content = t_content;
    }

}
