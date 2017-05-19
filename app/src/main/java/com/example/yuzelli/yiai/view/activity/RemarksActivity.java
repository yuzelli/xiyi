package com.example.yuzelli.yiai.view.activity;

import android.content.Context;
import android.content.Intent;
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
import com.example.yuzelli.yiai.bean.Goods;
import com.example.yuzelli.yiai.bean.Order;
import com.example.yuzelli.yiai.bean.OrderGoodsSettlement;
import com.example.yuzelli.yiai.constants.ConstantUtils;
import com.example.yuzelli.yiai.https.OkHttpClientManager;
import com.example.yuzelli.yiai.uitls.BaiduLoading;
import com.example.yuzelli.yiai.uitls.CommonAdapter;
import com.example.yuzelli.yiai.uitls.GsonUtils;
import com.example.yuzelli.yiai.uitls.ViewHolder;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Request;

public class RemarksActivity extends BaseActivity {
    private ImageView img_back;
    private ListView lv_goods;
    private List<OrderGoodsSettlement> goodLists;
    private RemarksHandler handler;
    private TextView tv_commit;
    private EditText et_input;
    private Order order;
    @Override
    protected int layoutInit() {
        return R.layout.activity_remarks;
    }

    @Override
    protected void binEvent() {
        handler =new  RemarksHandler();
        Intent intent =getIntent();
        order = (Order) intent.getSerializableExtra("order");
        img_back = (ImageView) this.findViewById(R.id.img_back);
        tv_commit = (TextView) this.findViewById(R.id.tv_commit);
        et_input = (EditText) this.findViewById(R.id.et_input);
        lv_goods = (ListView) this.findViewById(R.id.lv_goods);
        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        getOrderGoodsByOrderID();
        et_input.setText(order.getO_remarks());
        tv_commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = et_input.getText().toString().trim();
                if (content==null||content.equals("")){
                    showToast("未提交评价");
                    return;
                }
                doContent(content);
            }
        });
    }

    private void doContent(String content) {
        BaiduLoading.onBeiginDialog(this);
        OkHttpClientManager manager = OkHttpClientManager.getInstance();
        StringBuffer buffer = new StringBuffer(ConstantUtils.USER_ADDRESS).append(ConstantUtils.ORDER_REGISTER);
        Map<String, String> map = new HashMap<>();
        map.put("type", "updataOrder");
        map.put("business_id", order.getBusiness_id() + "");
        map.put("user_id", order.getUser_id() + "");
        map.put("o_remarks", content);
        map.put("o_pay", order.getO_pay()+"");
        map.put("o_creattime", order.getO_creattime() + "");
        map.put("order_id", order.getOrder_id() + "");
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
                    handler.sendEmptyMessage(ConstantUtils.GOOD_PAY_GOOD_GET_DATA);
                } else {
                    showToast("用户名或密码错误！");
                }
            }
        });
    }

    private void getOrderGoodsByOrderID() {
        OkHttpClientManager manager = OkHttpClientManager.getInstance();
        StringBuffer buffer = new StringBuffer(ConstantUtils.USER_ADDRESS).append(ConstantUtils.ORDERGOODS_REGISTER);
        Map<String, String> map = new HashMap<>();
        map.put("type", "findAllOrderGoodsByOrderID");
        map.put("order_id", order.getOrder_id() + "");
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
                    goodLists = GsonUtils.jsonToArrayList(body,OrderGoodsSettlement.class);
                    handler.sendEmptyMessage(ConstantUtils.SETTLEMENT_GOOD_GET_DATA);
                } else {
                    showToast("用户名或密码错误！");
                }
            }
        });
    }

    public static void  actionStart(Context context , Order order){
        Intent intent = new Intent(context,RemarksActivity.class);
        intent.putExtra("order",order);
        context.startActivity(intent);
    }

    class RemarksHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case ConstantUtils.SETTLEMENT_GOOD_GET_DATA:
                    updataList();
                    break;
                case ConstantUtils.GOOD_PAY_GOOD_GET_DATA:
                    showToast("评价已提交！");
                    BaiduLoading.onStopDialog();
                    break;
                default:
                    break;
            }
        }
    }

    private void updataList() {
        lv_goods.setAdapter(new CommonAdapter<OrderGoodsSettlement>(this,goodLists,R.layout.cell_sett_goods_item) {
            @Override
            public void convert(ViewHolder helper, OrderGoodsSettlement item, int position) {
                helper.setText(R.id.tv_name,item.getGood().getG_name()+"");
                helper.setText(R.id.tv_price,"单价："+item.getGood().getG_price()+"");
                helper.setText(R.id.tv_number,"数量："+item.getOg_number()+"");
                helper.setText(R.id.tv_allPrice,"总价："+item.getGood().getG_price()*item.getOg_number()+"");
            }
        });
    }
}
