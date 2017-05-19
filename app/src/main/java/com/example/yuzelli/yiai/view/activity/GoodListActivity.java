package com.example.yuzelli.yiai.view.activity;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.yuzelli.yiai.R;
import com.example.yuzelli.yiai.base.BaseActivity;
import com.example.yuzelli.yiai.bean.Business;
import com.example.yuzelli.yiai.bean.Goods;
import com.example.yuzelli.yiai.bean.Order;
import com.example.yuzelli.yiai.bean.OrderGoods;
import com.example.yuzelli.yiai.bean.UserInfo;
import com.example.yuzelli.yiai.constants.ConstantUtils;
import com.example.yuzelli.yiai.https.OkHttpClientManager;
import com.example.yuzelli.yiai.uitls.BaiduLoading;
import com.example.yuzelli.yiai.uitls.CommonAdapter;
import com.example.yuzelli.yiai.uitls.GsonUtils;
import com.example.yuzelli.yiai.uitls.SharePreferencesUtil;
import com.example.yuzelli.yiai.uitls.ViewHolder;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Request;

public class GoodListActivity extends BaseActivity {
    private Business business;
    private TextView tv_total;
    private TextView tv_count;
    private TextView tv_name;
    private ListView lv_goods;
    private CommonAdapter<Goods> adapter;
    private List<Goods> goodLists;
    private GoodListHandler handler;
    private UserInfo userInfo;
    private Order order;
    private OrderGoods ordergoods;
    private Context context;

    List<Integer> numberList;
    List<Boolean> flagList;
    @Override
    protected int layoutInit() {
        return R.layout.activity_good_list;
    }

    @Override
    protected void binEvent() {
        context = this;
        Intent intent = getIntent();
        handler = new GoodListHandler();
        business = (Business) intent.getSerializableExtra("business");
        userInfo = (UserInfo) SharePreferencesUtil.readObject(this,ConstantUtils.USER_LOGIN_INFO);
        this.findViewById(R.id.img_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tv_total = (TextView) this.findViewById(R.id.tv_total);
        tv_name = (TextView) this.findViewById(R.id.tv_name);
        tv_count = (TextView) this.findViewById(R.id.tv_count);
        lv_goods = (ListView) this.findViewById(R.id.lv_goods);
        tv_name.setText(business.getB_name());

        doGetGoodsList();

        tv_count.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (order==null){
                    showToast("订单未创建");
                    return;
                }
                boolean flag = true;
                for (boolean b:flagList){
                    if (b==false){
                        flag = b;
                    }
                }
                if (flag){
                    showToast("购物车中没有商品！");
                    return;
                }
                int allPice = 0;
                for (int i = 0 ; i < goodLists.size(); i++){
                    if (flagList.get(i)==false){
                        int number = numberList.get(i);
                        allPice=allPice+number *goodLists.get(i).getG_price();
                    }
                }
                SettlementActivity.actionStart(context,order,allPice);
                finish();

            }
        });
    }
    /**获取商品列表*/
    private void doGetGoodsList() {
        BaiduLoading.onBeiginDialog(this);
        OkHttpClientManager manager = OkHttpClientManager.getInstance();
        StringBuffer buffer = new StringBuffer(ConstantUtils.USER_ADDRESS).append(ConstantUtils.GOODS_REGISTER);
        Map<String, String> map = new HashMap<>();
        map.put("type", "findgoodbybusiness");
        map.put("business_id", business.getBusiness_id() + "");
        String url = OkHttpClientManager.attachHttpGetParams(buffer.toString(), map);
        manager.getAsync(url, new OkHttpClientManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, IOException e) {
                showToast("加载网路数据失败！");
            }

            @Override
            public void requestSuccess(String result) throws Exception {
                JSONObject object = new JSONObject(result);
                String flag = object.getString("error");
                if (flag.equals("ok")) {
                    String body = object.getString("object");
                    goodLists = GsonUtils.jsonToArrayList(body, Goods.class);
                    handler.sendEmptyMessage(ConstantUtils.GOOD_GET_ALL_DATA);
                } else {
                    showToast("用户名或密码错误！");
                }
            }
        });
    }


    private void updataList() {

        adapter = new CommonAdapter<Goods>(this, goodLists, R.layout.cell_good_items) {
            @Override
            public void convert(ViewHolder helper, Goods item, final int position) {
                helper.setText(R.id.tv_name, item.getG_name());
                helper.setText(R.id.tv_price, "单价：" + item.getG_price());
                final EditText shop_car_item_editNum_spec = helper.getView(R.id.shop_car_item_editNum_spec);
                ImageView shop_car_item_min_spec = helper.getView(R.id.shop_car_item_min_spec);
                shop_car_item_min_spec.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (numberList.get(0)==0){return;}
                         numberList.set(position,numberList.get(position)-1);
                        shop_car_item_editNum_spec.setText(numberList.get(position)+"");
                    }
                });

                ImageView shop_car_item_sum_spec = helper.getView(R.id.shop_car_item_sum_spec);
                shop_car_item_sum_spec.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        numberList.set(position,numberList.get(position)+1);
                        shop_car_item_editNum_spec.setText(numberList.get(position)+"");
                    }
                });
                shop_car_item_editNum_spec.setText(numberList.get(position)+"");
                TextView tv_add = helper.getView(R.id.tv_add);
               if (flagList.get(position).booleanValue()){
                   tv_add.setText("加入购物车");
               }else {
                   tv_add.setText("已加入");
               }
                tv_add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (order!=null){
                            if (flagList.get(position)&&numberList.get(position)!=0) {
                                doAddOrderGood(position, numberList.get(position));
                            }else {
                                showToast("已加入");
                            }
                        }else {
                            showToast("订单创建未创建,请再次添加");
                            doCreateOrder();
                        }
                    }
                });
            }
        };
        lv_goods.setAdapter(adapter);
    }

    /**加入购物车*/
    private void doAddOrderGood(final int postion, int number) {
        BaiduLoading.onBeiginDialog(this);
        OkHttpClientManager manager = OkHttpClientManager.getInstance();
        StringBuffer buffer = new StringBuffer(ConstantUtils.USER_ADDRESS).append(ConstantUtils.ORDERGOODS_REGISTER);
        Map<String, String> map = new HashMap<>();
        map.put("type", "addOrderGoods");
        map.put("order_id", order.getOrder_id()+"");
        map.put("good_id", goodLists.get(postion).getGood_id()+"");
        map.put("og_number",number+"");
        String url = OkHttpClientManager.attachHttpGetParams(buffer.toString(), map);
        manager.getAsync(url, new OkHttpClientManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, IOException e) {
                showToast("加载网路数据失败！");
            }

            @Override
            public void requestSuccess(String result) throws Exception {

                JSONObject object = new JSONObject(result);
                String flag = object.getString("error");
                if (flag.equals("ok")) {
                    String body = object.getString("object");
                    ordergoods = (OrderGoods) GsonUtils.getInstanceByJson(OrderGoods.class,body);
                    Message message = new Message();
                    message.what = ConstantUtils.ORDERGOODS_CREATE_GET_DATA;
                    message.obj = postion;
                    handler.sendMessage(message);
                } else {
                    showToast("用户名或密码错误！");
                }
            }
        });
    }
    /**创建订单*/
    private void doCreateOrder() {
        OkHttpClientManager manager = OkHttpClientManager.getInstance();
        StringBuffer buffer = new StringBuffer(ConstantUtils.USER_ADDRESS).append(ConstantUtils.ORDER_REGISTER);
        Map<String, String> map = new HashMap<>();
        map.put("type", "addOrder");
        map.put("business_id", business.getBusiness_id()+"");
        map.put("user_id", userInfo.getUser_id()+"");
        map.put("o_pay", "0");
        map.put("o_remarks", "未评价");
        map.put("o_creattime", System.currentTimeMillis()+"");
        String url = OkHttpClientManager.attachHttpGetParams(buffer.toString(), map);
        manager.getAsync(url, new OkHttpClientManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, IOException e) {
                showToast("加载网路数据失败！");
            }

            @Override
            public void requestSuccess(String result) throws Exception {

                JSONObject object = new JSONObject(result);
                String flag = object.getString("error");
                if (flag.equals("ok")) {
                    String body = object.getString("object");
                    order = (Order) GsonUtils.getInstanceByJson(Order.class,body);
                    handler.sendEmptyMessage(ConstantUtils.ORDER_CREATE_GET_DATA);
                } else {
                    showToast("用户名或密码错误！");
                }
            }
        });
    }

    /**获取订单详情*/
    private void getOrderGoodsList(){

    }
    class GoodListHandler extends Handler {
        private int postion;

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case ConstantUtils.GOOD_GET_ALL_DATA:

                    numberList = new ArrayList<>();
                    flagList = new ArrayList<>();
                    for (int i = 0 ; i <goodLists.size();i++){
                        numberList.add(0);
                    }
                    for (int i = 0 ; i <goodLists.size();i++){
                        flagList.add(true);
                    }
                    updataList();
                    doCreateOrder();
                    break;
                case ConstantUtils.ORDER_CREATE_GET_DATA:
                    showToast("订单创建成功！可以加入购物车了！");
                    BaiduLoading.onStopDialog();
                    break;
                case ConstantUtils.ORDERGOODS_CREATE_GET_DATA:
                    showToast("加入购物车成功");
                    this.postion = (int) msg.obj;
                    flagList.set(postion,false);
                    adapter.notifyDataSetChanged();
                    BaiduLoading.onStopDialog();
                    jishuan();
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 计算价格
     */
    private void jishuan() {
        int allPice = 0;
        int allNum = 0;
        for (int i = 0 ; i < goodLists.size(); i++){
            if (flagList.get(i)==false){
                int number = numberList.get(i);
                allNum+=number;
                allPice=allPice+number *goodLists.get(i).getG_price();
            }
        }
        tv_total.setText("合计："+allPice+"元");
        tv_count.setText("去结算（"+allNum+"）");
    }

    public static void actionStart(Context context, Business business) {
        Intent intent = new Intent(context, GoodListActivity.class);
        intent.putExtra("business", business);
        context.startActivity(intent);
    }
}
