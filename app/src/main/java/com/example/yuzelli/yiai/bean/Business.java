package com.example.yuzelli.yiai.bean;

import java.io.Serializable;
import java.util.List;


public class Business implements Serializable{

	private int business_id;
	private String b_name;
	private String b_notice;
	private String b_imgurl;
	private String b_phone;
	private String b_address;
	public int getBusiness_id() {
		return business_id;
	}
	public void setBusiness_id(int business_id) {
		this.business_id = business_id;
	}
	public String getB_name() {
		return b_name;
	}
	public void setB_name(String b_name) {
		this.b_name = b_name;
	}
	public String getB_notice() {
		return b_notice;
	}
	public void setB_notice(String b_notice) {
		this.b_notice = b_notice;
	}
	public String getB_imgurl() {
		return b_imgurl;
	}
	public void setB_imgurl(String b_imgurl) {
		this.b_imgurl = b_imgurl;
	}
	public String getB_phone() {
		return b_phone;
	}
	public void setB_phone(String b_phone) {
		this.b_phone = b_phone;
	}
	public String getB_address() {
		return b_address;
	}
	public void setB_address(String b_address) {
		this.b_address = b_address;
	}
	
	
}
