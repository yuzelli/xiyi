package com.example.yuzelli.yiai.view.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.icu.text.Collator;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.yuzelli.yiai.R;
import com.example.yuzelli.yiai.base.BaseActivity;
import com.example.yuzelli.yiai.bean.Goods;
import com.example.yuzelli.yiai.bean.Order;
import com.example.yuzelli.yiai.bean.OrderGoodsSettlement;
import com.example.yuzelli.yiai.bean.TimeBean;
import com.example.yuzelli.yiai.constants.ConstantUtils;
import com.example.yuzelli.yiai.https.OkHttpClientManager;
import com.example.yuzelli.yiai.uitls.BaiduLoading;
import com.example.yuzelli.yiai.uitls.CommonAdapter;
import com.example.yuzelli.yiai.uitls.DateTimePickDialogUtil;
import com.example.yuzelli.yiai.uitls.GsonUtils;
import com.example.yuzelli.yiai.uitls.ViewHolder;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Request;


public class SettlementActivity extends BaseActivity {
    private ListView lv_goods;
    private ListView lv_time;
    private ImageView img_add;
    private TextView tv_pay;
    private Order order;
    private List<OrderGoodsSettlement> goodsList;
    private List<TimeBean> timesList;
    private SetlementHandler handler;
    private String startTime = "";
    private String endTime = "";
    private Context context;
    @Override
    protected int layoutInit() {
        return R.layout.activity_settlement;
    }

    @Override
    protected void binEvent() {
        Intent intent = getIntent();
        context =this;
        handler = new SetlementHandler();
        order = (Order) intent.getSerializableExtra("myOrder");
        this.findViewById(R.id.img_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        lv_goods= (ListView) this.findViewById(R.id.lv_goods);
        lv_time= (ListView) this.findViewById(R.id.lv_time);
        img_add= (ImageView) this.findViewById(R.id.img_add);
        tv_pay= (TextView) this.findViewById(R.id.tv_pay);
        tv_pay.setText("支付："+intent.getIntExtra("allPrice",0)+"");
        getOrderGoodsByOrderID();
        img_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupSearchWindow();
            }
        });
        tv_pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("提示框");
                builder.setMessage("你确定要支付么");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        doPayAction();
                    }
                });
                builder.setNegativeButton("取消", null);
                builder.show();

            }
        });
    }

    private void doPayAction() {
        BaiduLoading.onBeiginDialog(this);
        OkHttpClientManager manager = OkHttpClientManager.getInstance();
        StringBuffer buffer = new StringBuffer(ConstantUtils.USER_ADDRESS).append(ConstantUtils.ORDER_REGISTER);
        Map<String, String> map = new HashMap<>();
        map.put("type", "updataOrder");
        map.put("business_id", order.getBusiness_id() + "");
        map.put("user_id", order.getUser_id() + "");
        map.put("o_remarks", order.getO_remarks() + "");
        map.put("o_pay", 1 + "");
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
                    goodsList = GsonUtils.jsonToArrayList(body,OrderGoodsSettlement.class);
                    handler.sendEmptyMessage(ConstantUtils.SETTLEMENT_GOOD_GET_DATA);
                } else {
                    showToast("用户名或密码错误！");
                }
            }
        });
    }

    /**
     * 搜索条件popupWindow
     */
    private void showPopupSearchWindow() {
        final View contentView = LayoutInflater.from(this).inflate(R.layout.popup_order_search, null);
        final PopupWindow searchPopup = new PopupWindow();
        searchPopup.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        searchPopup.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        searchPopup.setContentView(contentView);//设置包含视图


        final TextView tvBeginTime = (TextView) contentView.findViewById(R.id.tv_begin_time);
        final TextView tvEndTime = (TextView) contentView.findViewById(R.id.tv_end_time);

        tvBeginTime.setText(startTime);
        tvEndTime.setText(endTime);


        contentView.findViewById(R.id.img_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchPopup.dismiss();
            }
        });

        contentView.findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchPopup.dismiss();
            }
        });
        contentView.findViewById(R.id.tv_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 doAddTime(tvBeginTime.getText().toString().trim(),tvEndTime.getText().toString().trim());
                searchPopup.dismiss();
            }
        });

        tvBeginTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDataPicker(tvBeginTime);
            }
        });
        tvEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDataPicker(tvEndTime);
            }
        });

        // 控制popupwindow点击屏幕其他地方消失
        searchPopup.setBackgroundDrawable(this.getResources().getDrawable(
                R.drawable.bg_popupwindow));
        searchPopup.setFocusable(true);
        searchPopup.setOutsideTouchable(true);// 触摸popupwindow外部，popupwindow消失。这个要求你的popupwindow要有背景图片才可以成功，如上
        View rootview = LayoutInflater.from(this).inflate(R.layout.activity_settlement, null);
        // searchPopup.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);//设置模式，和Activity的一样，覆盖，调整大小。
        searchPopup.showAtLocation(rootview, Gravity.CENTER, 0, 0);
        backgroundAlpha(0.5f);
        searchPopup.setOnDismissListener(new poponDismissListener());
    }

    private void doAddTime(String begin,String end) {
        OkHttpClientManager manager = OkHttpClientManager.getInstance();
        StringBuffer buffer = new StringBuffer(ConstantUtils.USER_ADDRESS).append(ConstantUtils.TIME_REGISTER);
        Map<String, String> map = new HashMap<>();
        map.put("type", "addTime");
        map.put("order_id", order.getOrder_id() + "");
        map.put("t_content", begin+"\n"+end);
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

                    handler.sendEmptyMessage(ConstantUtils.TIMELIST_ADD_GOOD_GET_DATA);
                } else {
                    showToast("用户名或密码错误！");
                }
            }
        });
    }

    /**
     * 添加新笔记时弹出的popWin关闭的事件，主要是为了将背景透明度改回来
     *
     * @author cg
     */
    class poponDismissListener implements PopupWindow.OnDismissListener {

        @Override
        public void onDismiss() {
            // TODO Auto-generated method stub
            //Log.v("List_noteTypeActivity:", "我是关闭事件");
            backgroundAlpha(1f);
        }

    }
    /**
     * 设置添加屏幕的背景透明度
     *
     * @param bgAlpha
     */
    public void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
       getWindow().setAttributes(lp);
    }
    /**
     * 时间选择器
     */
    private void showDataPicker( TextView tv_time) {
        DateTimePickDialogUtil dateTimePicKDialog = new DateTimePickDialogUtil(
                this, tv_time.getText().toString().trim());
        dateTimePicKDialog.dateTimePicKDialog( tv_time);
    }


    class SetlementHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case ConstantUtils.SETTLEMENT_GOOD_GET_DATA:
                    updataGoodList();
                    doGetTimeList();
                    break;
                case ConstantUtils.TIMELIST_GOOD_GET_DATA:
                    updataTimeList();
                    break;
                case ConstantUtils.TIMELIST_ADD_GOOD_GET_DATA:
                    doGetTimeList();
                    break;
                case ConstantUtils.GOOD_PAY_GOOD_GET_DATA:
                    BaiduLoading.onStopDialog();
                    finish();
                    break;
                default:
                    break;
            }
        }
    }

    private void updataTimeList() {
        lv_time.setAdapter(new CommonAdapter<TimeBean>(this,timesList,R.layout.cell_time_time) {
            @Override
            public void convert(ViewHolder helper, TimeBean item, int position) {
                helper.setText(R.id.tv_content,item.getT_content());
            }
        });
    }

    private void doGetTimeList() {
        OkHttpClientManager manager = OkHttpClientManager.getInstance();
        StringBuffer buffer = new StringBuffer(ConstantUtils.USER_ADDRESS).append(ConstantUtils.TIME_REGISTER);
        Map<String, String> map = new HashMap<>();
        map.put("type", "findTimeByOrderID");
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
                    timesList = GsonUtils.jsonToArrayList(body,TimeBean.class);
                    handler.sendEmptyMessage(ConstantUtils.TIMELIST_GOOD_GET_DATA);
                } else {
                    showToast("用户名或密码错误！");
                }
            }
        });
    }

    private void updataGoodList() {
        lv_goods.setAdapter(new CommonAdapter<OrderGoodsSettlement>(this,goodsList,R.layout.cell_sett_goods_item) {
            @Override
            public void convert(ViewHolder helper, OrderGoodsSettlement item, int position) {
                helper.setText(R.id.tv_name,item.getGood().getG_name()+"");
                helper.setText(R.id.tv_price,"单价："+item.getGood().getG_price()+"");
                helper.setText(R.id.tv_number,"数量："+item.getOg_number()+"");
                helper.setText(R.id.tv_allPrice,"总价："+item.getGood().getG_price()*item.getOg_number()+"");
            }
        });
        int allPrice = 0;
        for (OrderGoodsSettlement o:goodsList){
            int price = o.getGood().getG_price();
            int num  = o .getOg_number();
            allPrice = allPrice+price*num;
        }
        tv_pay.setText("支付："+allPrice+"");

    }


    public static void actionStart(Context context, Order myOrder, int allPrice) {
        Intent intent = new Intent(context, SettlementActivity.class);
        intent.putExtra("myOrder", myOrder);
        intent.putExtra("allPrice", allPrice);
        context.startActivity(intent);
    }


}
