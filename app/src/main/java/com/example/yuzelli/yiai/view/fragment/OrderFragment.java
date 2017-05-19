package com.example.yuzelli.yiai.view.fragment;

import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.yuzelli.yiai.R;
import com.example.yuzelli.yiai.base.BaseFragment;
import com.example.yuzelli.yiai.bean.Order;
import com.example.yuzelli.yiai.bean.UserInfo;
import com.example.yuzelli.yiai.constants.ConstantUtils;
import com.example.yuzelli.yiai.https.OkHttpClientManager;
import com.example.yuzelli.yiai.uitls.BaiduLoading;
import com.example.yuzelli.yiai.uitls.CommonAdapter;
import com.example.yuzelli.yiai.uitls.DateUtils;
import com.example.yuzelli.yiai.uitls.GsonUtils;
import com.example.yuzelli.yiai.uitls.SharePreferencesUtil;
import com.example.yuzelli.yiai.uitls.ViewHolder;
import com.example.yuzelli.yiai.view.activity.MainActivity;
import com.example.yuzelli.yiai.view.activity.RemarksActivity;
import com.example.yuzelli.yiai.view.activity.SettlementActivity;

import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Request;

public class OrderFragment extends BaseFragment {
    private ListView lv_goods;
    private UserInfo userInfo;
    private OrderFragmentHandler handler;
    private List<Order> orderList;

    @Override
    protected int layoutInit() {
        return R.layout.fragment_order;
    }

    @Override
    protected void bindEvent(View view) {
        handler = new OrderFragmentHandler();
        userInfo = (UserInfo) SharePreferencesUtil.readObject(getActivity(), ConstantUtils.USER_LOGIN_INFO);
        lv_goods = (ListView) view.findViewById(R.id.lv_goods);

    }

    @Override
    public void onResume() {
        super.onResume();
        doOrderGoods();
    }

    private void doOrderGoods() {
        BaiduLoading.onBeiginDialog(getActivity());
        OkHttpClientManager manager = OkHttpClientManager.getInstance();
        StringBuffer buffer = new StringBuffer(ConstantUtils.USER_ADDRESS).append(ConstantUtils.ORDER_REGISTER);
        Map<String, String> map = new HashMap<>();
        map.put("type", "findAllOrderByUserID");
        map.put("user_id", userInfo.getUser_id() + "");
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
                    orderList = GsonUtils.jsonToArrayList(body, Order.class);
                    handler.sendEmptyMessage(ConstantUtils.USER_ORDER_GOOD_GET_DATA);
                } else {
                    showToast("用户名或密码错误！");
                }
            }
        });
    }

    class OrderFragmentHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case ConstantUtils.USER_ORDER_GOOD_GET_DATA:
                    updataList();
                    BaiduLoading.onStopDialog();
                    break;
                default:
                    break;
            }
        }
    }

    private void updataList() {
        lv_goods.setAdapter(new CommonAdapter<Order>(getActivity(), orderList, R.layout.cell_user_order) {
            @Override
            public void convert(ViewHolder helper, Order item, int position) {
                helper.setText(R.id.tv_name, DateUtils.converTime(Long.valueOf(item.getO_creattime()))+"");
                helper.setText(R.id.tv_username, userInfo.getU_name());
                helper.setText(R.id.tv_address,userInfo.getU_address());
                if (item.getO_pay() == 0) {
                    helper.setText(R.id.tv_stata, "未支付");
                } else {
                    helper.setText(R.id.tv_stata, "已支付");
                }
            }
        });
        lv_goods.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (orderList.get(position).getO_pay()==0){
                    SettlementActivity.actionStart(getActivity(),orderList.get(position),0);
                }else {
                    RemarksActivity.actionStart(getActivity(),orderList.get(position));
                }
            }
        });
    }


    @Override
    protected void fillData() {

    }
}
