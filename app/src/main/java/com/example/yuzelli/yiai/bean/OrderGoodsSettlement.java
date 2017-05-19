package com.example.yuzelli.yiai.bean;



public class OrderGoodsSettlement {


    /**
     * business_id : 3
     * g_name : 西服
     * g_price : 36
     * good_id : 19
     */

    private GoodBean good;
    /**
     * good : {"business_id":3,"g_name":"西服","g_price":36,"good_id":19}
     * og_number : 5
     * order_id : 25
     * ordergoods_id : 17
     */

    private int og_number;
    private int order_id;
    private int ordergoods_id;

    public GoodBean getGood() {
        return good;
    }

    public void setGood(GoodBean good) {
        this.good = good;
    }

    public int getOg_number() {
        return og_number;
    }

    public void setOg_number(int og_number) {
        this.og_number = og_number;
    }

    public int getOrder_id() {
        return order_id;
    }

    public void setOrder_id(int order_id) {
        this.order_id = order_id;
    }

    public int getOrdergoods_id() {
        return ordergoods_id;
    }

    public void setOrdergoods_id(int ordergoods_id) {
        this.ordergoods_id = ordergoods_id;
    }

    public static class GoodBean {
        private int business_id;
        private String g_name;
        private int g_price;
        private int good_id;

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

        public int getGood_id() {
            return good_id;
        }

        public void setGood_id(int good_id) {
            this.good_id = good_id;
        }
    }
}
