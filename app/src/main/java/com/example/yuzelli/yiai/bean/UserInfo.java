package com.example.yuzelli.yiai.bean;

import java.io.Serializable;



public class UserInfo implements Serializable {

    private int user_id;
    private String u_imgurl;
    private String u_phone;
    private String u_password;
    private String u_name;
    private String u_address;
    public int getUser_id() {
        return user_id;
    }
    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }
    public String getU_imgurl() {
        return u_imgurl;
    }
    public void setU_imgurl(String u_imgurl) {
        this.u_imgurl = u_imgurl;
    }
    public String getU_phone() {
        return u_phone;
    }
    public void setU_phone(String u_phone) {
        this.u_phone = u_phone;
    }
    public String getU_password() {
        return u_password;
    }
    public void setU_password(String u_password) {
        this.u_password = u_password;
    }
    public String getU_name() {
        return u_name;
    }
    public void setU_name(String u_name) {
        this.u_name = u_name;
    }
    public String getU_address() {
        return u_address;
    }
    public void setU_address(String u_address) {
        this.u_address = u_address;
    }



}
