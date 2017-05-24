package com.example.yuzelli.yiai.constants;

/**
 * 常量类
 */

public class ConstantUtils
{
    //闪屏页
    public static final int SPLASH_START_ACTIVITY = 0x00001000;
    //用户注册成功
    public static final int REGISTER_GET_DATA = 0x00001001;
    //用户登录成功
    public static final int LOGIN_GET_DATA = 0x00001002;
    //获取商家列表
    public static final int BUSINESS_GET_ALL_DATA = 0x00001003;
   //获取商家对应的商品
    public static final int GOOD_GET_ALL_DATA = 0x00001004;
    //创建订单
    public static final int ORDER_CREATE_GET_DATA = 0x00001005;
    //加入购物车
    public static final int ORDERGOODS_CREATE_GET_DATA = 0x00001006;
    //订单页获取数据
    public static final int SETTLEMENT_GOOD_GET_DATA = 0x00001007;
    //时间获取数据
    public static final int TIMELIST_GOOD_GET_DATA = 0x00001008;
    //时间获取添加
    public static final int TIMELIST_ADD_GOOD_GET_DATA = 0x00001009;
    //时间获取添加
    public static final int GOOD_PAY_GOOD_GET_DATA = 0x00001010;
    //获取用户订单
    public static final int USER_ORDER_GOOD_GET_DATA = 0x00001011;
    public static final int PERSONCOMPILE_UP_DATA = 0x00001012;
//    SharedPerference
    //是否是用户第一次登录
    public static final String FIRST_LOGIN_SP = "PhoneHelperFirstLoginShared";
    public static final String FIRST_LOGIN_KEY = "firstUse" ;
    //登录用户信息
    public static final String USER_LOGIN_INFO = "UserInfo";
    //自己的后台
   //public static final String USER_ADDRESS = "http://192.168.0.102:8080/YIAIService/";
   public static final String USER_ADDRESS = "http://10.89.35.3:8080/YIAIService/";
    //用户操作
    public static final String USER_REGISTER = "UserInfoServlet";
    public static final String BUSINESS_REGISTER = "BuninessServlet";
    public static final String GOODS_REGISTER = "GoodServlet";
    public static final String ORDER_REGISTER = "OrderServlet";
    public static final String ORDERGOODS_REGISTER = "OrderGoodServlet";
    public static final String TIME_REGISTER = "TimeServlet";
    //用户头像存放文件名
    public static final String AVATAR_FILE_PATH = "/userHeadImg.jpg";
    //七牛
    public static final String QN_ACCESSKEY = "1lz3oyLnZAMG3r0o6hsRUY_U45E58nb9-Q2mCzp8";
    public static final String QN_SECRETKEY = "1no9Tx1bAHSOC0g3xABHhsYXPbRKX_v3o_uGI0Nv";
    public static final String QN_IMG_ADDRESS = "http://ojterpx44.bkt.clouddn.com/";



}
