package com.example.yuzelli.yiai.view.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.RippleDrawable;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.yuzelli.yiai.R;
import com.example.yuzelli.yiai.base.BaseActivity;
import com.example.yuzelli.yiai.bean.Business;
import com.example.yuzelli.yiai.bean.Order;
import com.example.yuzelli.yiai.constants.ConstantUtils;
import com.example.yuzelli.yiai.https.OkHttpClientManager;
import com.example.yuzelli.yiai.uitls.BaiduLoading;
import com.example.yuzelli.yiai.uitls.CommonAdapter;
import com.example.yuzelli.yiai.uitls.GsonUtils;
import com.example.yuzelli.yiai.uitls.ViewHolder;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Request;

public class BusinessDetailActivity extends BaseActivity {
    private ImageView img_back;
    private TextView tv_name;
    private TextView tv_phone;
    private TextView tv_address;
    private TextView tv_notice;
    private ListView lv_comment;
    private ImageView img_business;
    private List<Order> orderLists;

    private BDHandler handler;

    private Business business;
    @Override
    protected int layoutInit() {
        return R.layout.activity_business_detail;
    }

    @Override
    protected void binEvent() {
        handler = new BDHandler();
        img_back = (ImageView) this.findViewById(R.id.img_back);
        tv_name = (TextView) this.findViewById(R.id.tv_name);
        tv_phone = (TextView) this.findViewById(R.id.tv_phone);
        tv_address = (TextView) this.findViewById(R.id.tv_address);
        tv_notice = (TextView) this.findViewById(R.id.tv_notice);
        lv_comment = (ListView) this.findViewById(R.id.lv_comment);
        img_business = (ImageView) this.findViewById(R.id.img_business);
        Intent intent = getIntent();
        business = (Business) intent.getSerializableExtra("business");

        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tv_name.setText(business.getB_name());
        tv_phone.setText(business.getB_phone());
        tv_address.setText(business.getB_address());
        tv_notice.setText(business.getB_notice());

        DisplayImageOptions options = new DisplayImageOptions.Builder()
//                .showImageOnLoading(R.mipmap.icon_loading_64px)
//                .showImageOnFail(R.mipmap.icon_error_64px)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
        ImageLoader.getInstance().loadImage(business.getB_imgurl(),options,new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String s, View view) {

            }

            @Override
            public void onLoadingFailed(String s, View view, FailReason failReason) {

            }

            @Override
            public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                img_business.setImageBitmap(bitmap);
            }

            @Override
            public void onLoadingCancelled(String s, View view) {

            }
        });

        doGetGoods();
    }

    private void doGetGoods() {
        BaiduLoading.onBeiginDialog(this);
        OkHttpClientManager manager = OkHttpClientManager.getInstance();
        StringBuffer buffer = new StringBuffer(ConstantUtils.USER_ADDRESS).append(ConstantUtils.ORDER_REGISTER);
        Map<String, String> map = new HashMap<>();
        map.put("type", "findAllOrderByBuninessID");
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
                    orderLists = GsonUtils.jsonToArrayList(body,Order.class);
                    handler.sendEmptyMessage(22);
                } else {
                    showToast("用户名或密码错误！");
                }
            }
        });
    }
    class BDHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 22:
                    updataList();
                    BaiduLoading.onStopDialog();
                    break;

                default:
                    break;
            }
        }
    }

    private void updataList() {
        lv_comment.setAdapter(new CommonAdapter<Order>(this,orderLists,R.layout.cell_item) {
            @Override
            public void convert(ViewHolder helper, Order item, int position) {
                helper.setText(R.id.tv_content,item.getO_remarks());
                helper.setText(R.id.tv_foot,(position+1)+"楼");
            }
        });
    }

    public static void actionStart(Context context, Business business){
        Intent intent = new Intent(context,BusinessDetailActivity.class);
        intent.putExtra("business",business);
        context.startActivity(intent);
    }
}
