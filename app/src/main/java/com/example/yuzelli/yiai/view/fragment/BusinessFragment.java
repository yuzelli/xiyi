package com.example.yuzelli.yiai.view.fragment;

import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.yuzelli.yiai.R;
import com.example.yuzelli.yiai.adapter.BannerAdapter;
import com.example.yuzelli.yiai.base.BaseFragment;
import com.example.yuzelli.yiai.bean.Business;
import com.example.yuzelli.yiai.bean.UserInfo;
import com.example.yuzelli.yiai.constants.ConstantUtils;
import com.example.yuzelli.yiai.https.OkHttpClientManager;
import com.example.yuzelli.yiai.uitls.CommonAdapter;
import com.example.yuzelli.yiai.uitls.GsonUtils;
import com.example.yuzelli.yiai.uitls.SharePreferencesUtil;
import com.example.yuzelli.yiai.uitls.ViewHolder;
import com.example.yuzelli.yiai.view.activity.BusinessDetailActivity;
import com.example.yuzelli.yiai.view.activity.GoodListActivity;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Request;

public class BusinessFragment extends BaseFragment implements View.OnTouchListener, ViewPager.OnPageChangeListener{
    private ListView lv_business_list;
    private View rootView;
    private List<Business> businessList;
    private BusinessHandler handler;

    ViewPager vp_picture;   //图片轮播

    TextView tv_vp_title;   //图片轮播的简介
    LinearLayout ll_Point;

    private BannerAdapter adapter;   //图片轮播adapter
    private ArrayList<ImageView> bannerImageDates;   //图片轮播的图片
    private int[] imgs = {R.drawable.scenery1, R.drawable.scenery2, R.drawable.scenery3};
    private View bottomView;
    private int currentIndex = 300;   //图片下标
    private long lastTime;           //上一次图片滚动时间
    UserInfo userInfo ;
    @Override
    protected int layoutInit() {
        return R.layout.fragment_business;
    }

    @Override
    protected void bindEvent(View view) {
        lv_business_list = (ListView) view.findViewById(R.id.lv_business_list);
        vp_picture = (ViewPager) view.findViewById(R.id.vp_picture);
        tv_vp_title = (TextView) view.findViewById(R.id.tv_vp_title);
        ll_Point = (LinearLayout) view.findViewById(R.id.ll_Point);
        userInfo = (UserInfo) SharePreferencesUtil.readObject(getActivity(),ConstantUtils.USER_LOGIN_INFO);
        handler = new BusinessHandler();
        doGetBusiness();
        updataBanner();

    }

    @Override
    public void onResume() {
        super.onResume();
        userInfo = (UserInfo) SharePreferencesUtil.readObject(getActivity(),ConstantUtils.USER_LOGIN_INFO);
    }

    /**
     * 更新图片轮播
     */
    private void updataBanner() {
        bannerImageDates = new ArrayList<>();
        for (int i = 0; i < imgs.length; i++) {
            ImageView img = new ImageView(getActivity());
            //显示图片的配置
            img.setImageResource(imgs[i]);
            img.setScaleType(ImageView.ScaleType.FIT_XY);
            bannerImageDates.add(img);
        }
        adapter = new BannerAdapter(getActivity(), bannerImageDates);
        vp_picture.setOnTouchListener(this);
        vp_picture.setAdapter(adapter);
        vp_picture.setCurrentItem(300);
        vp_picture.addOnPageChangeListener(this);
        handler.postDelayed(runnableForBanner, 2000);
        addPoint();
        vp_picture.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                monitorPoint(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    // 设置轮播时间间隔
    private Runnable runnableForBanner = new Runnable() {
        @Override
        public void run() {
            if (System.currentTimeMillis() - lastTime >= 3000) {
                vp_picture.setCurrentItem(currentIndex);
                currentIndex++;
                lastTime = System.currentTimeMillis();
            }
            handler.postDelayed(runnableForBanner, 3000);
        }
    };

    /**
     * 添加小圆点
     */
    private void addPoint() {
        // 1.根据图片多少，添加多少小圆点
        ll_Point.removeAllViews();

        for (int i = 0; i < imgs.length; i++) {
            LinearLayout.LayoutParams pointParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            pointParams.gravity = Gravity.CENTER_VERTICAL;
            if (i < 1) {
                pointParams.setMargins(0, 0, 0, 0);
            } else {
                pointParams.setMargins(10, 0, 0, 0);
            }
            ImageView iv = new ImageView(getActivity());
            iv.setLayoutParams(pointParams);
            iv.setBackgroundResource(R.drawable.point_normal);
            ll_Point.addView(iv);
        }
        ll_Point.getChildAt(0).setBackgroundResource(R.drawable.point_select);
    }

    /**
     * 判断小圆点
     *
     * @param position
     */
    private void monitorPoint(int position) {
        int current = (position - 300) % imgs.length;
        for (int i = 0; i < imgs.length; i++) {
            if (i == current) {
                ll_Point.getChildAt(current).setBackgroundResource(
                        R.drawable.point_select);
            } else {
                ll_Point.getChildAt(i).setBackgroundResource(
                        R.drawable.point_normal);
            }
        }

    }

    private void doGetBusiness() {
        OkHttpClientManager manager = OkHttpClientManager.getInstance();
        StringBuffer buffer = new StringBuffer(ConstantUtils.USER_ADDRESS).append(ConstantUtils.BUSINESS_REGISTER);
        Map<String, String> map = new HashMap<>();
        map.put("type", "findAllBuniness");
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
                    businessList = GsonUtils.jsonToArrayList(body, Business.class);
                    handler.sendEmptyMessage(ConstantUtils.BUSINESS_GET_ALL_DATA);
                } else {
                    showToast("查询失败！");
                }
            }
        });
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }
    String [] titles= {"周日好礼送不停","件件打8折","送货上门，贴心服务"};
    @Override
    public void onPageSelected(int position) {
        currentIndex = position;
        lastTime = System.currentTimeMillis();
        //设置轮播文字改变
        final int index = position % bannerImageDates.size();
        tv_vp_title.setText(titles[index]);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }

    class BusinessHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case ConstantUtils.BUSINESS_GET_ALL_DATA:
                   updataList();
                    break;
                default:
                    break;
            }
        }
    }

    private void updataList() {
        lv_business_list.setAdapter(new CommonAdapter<Business>(context,businessList,R.layout.cell_business_list) {
            @Override
            public void convert(ViewHolder helper, final Business item,int postion) {
                helper.setImageByUrl(R.id.img_business,item.getB_imgurl());
                helper.setText(R.id.tv_name,item.getB_name());
                helper.setText(R.id.tv_phone,item.getB_phone());
                helper.setText(R.id.tv_address,item.getB_address());
                TextView tv_goods = helper.getView(R.id.tv_goods);
                tv_goods.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (userInfo.getU_address()==null||userInfo.getU_address().equals("")||userInfo.getU_address().equals("未设置")){
                            showToast("请设置您的地址");
                            return;
                        }
                        if (userInfo.getU_name()==null||userInfo.getU_name().equals("")||userInfo.getU_name().equals("未设置")){
                            showToast("请设置您的收件人名字");
                            return;
                        }
                        GoodListActivity.actionStart(context,item);
                    }
                });
                TextView tv_business_detail = helper.getView(R.id.tv_business_detail);
                tv_business_detail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        BusinessDetailActivity.actionStart(context,item);
                    }
                });
            }
        });

    }

    @Override
    protected void fillData() {

    }


}
