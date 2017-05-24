package com.example.yuzelli.yiai.view.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yuzelli.yiai.R;
import com.example.yuzelli.yiai.base.BaseActivity;
import com.example.yuzelli.yiai.bean.UserInfo;
import com.example.yuzelli.yiai.constants.ConstantUtils;
import com.example.yuzelli.yiai.https.OkHttpClientManager;
import com.example.yuzelli.yiai.uitls.LoginUtils;
import com.example.yuzelli.yiai.uitls.SharePreferencesUtil;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Request;


public class LoginActivity extends BaseActivity {
    private  EditText et_phone;
    private  EditText et_password;
    private TextView tv_login;
    private  ImageView cb_agree;
    private boolean agreeFlag = true;
    private Context context;
    private UserInfo userInfo;
    private LoginHandler handler;



    @Override
    protected int layoutInit() {
        return R.layout.activity_login;
    }

    @Override
    protected void binEvent() {
        context = this;
        handler = new LoginHandler();
        et_phone = (EditText) this.findViewById(R.id.et_phone);
        et_password = (EditText) this.findViewById(R.id.et_password);
        tv_login = (TextView) this.findViewById(R.id.tv_login);

        this.findViewById(R.id.tv_register).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RegisterActivity.actionStart(context);
            }
        });
        cb_agree = (ImageView) this.findViewById(R.id.cb_agree);
        cb_agree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                agreeFlag = !agreeFlag;
                if (agreeFlag){
                    cb_agree.setImageResource(R.drawable.checkbox_pressed);
                }else {
                    cb_agree.setImageResource(R.drawable.checkbox_normal);
                }
            }
        });
        tv_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (agreeFlag) {
                    doLoginAction();
                } else {
                    Toast.makeText(context, "请同意协议", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void doLoginAction() {
        String phone = et_phone.getText().toString().trim();
        String password = et_password.getText().toString().trim();
        if (phone == null || phone.equals("")) {
            Toast.makeText(context, "请输入手机号", Toast.LENGTH_SHORT).show();
        }
        if (password == null || password.equals("")) {
            Toast.makeText(context, "请输入密码", Toast.LENGTH_SHORT).show();
        }
        if (LoginUtils.isPhoneEnable(phone)) {
           doLoginRequest(phone,password);
        } else {
            Toast.makeText(context, "请正确的输入手机号", Toast.LENGTH_SHORT).show();
        }
    }
    /**
      网络请求
     */
    private void doLoginRequest(String userPhone,String passWord) {
        OkHttpClientManager manager = OkHttpClientManager.getInstance();
        StringBuffer buffer = new StringBuffer(ConstantUtils.USER_ADDRESS).append(ConstantUtils.USER_REGISTER);
        Map<String, String> map = new HashMap<>();
        map.put("type", "login");
        map.put("u_phone", userPhone);
        map.put("u_password", passWord);
        String url = OkHttpClientManager.attachHttpGetParams(buffer.toString(), map);
        manager.getAsync(url, new OkHttpClientManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, IOException e) {
                showToast("加载网路数据失败！");
            }

            @Override
            public void requestSuccess(String result) throws Exception {
                Gson gson = new Gson();
                JSONObject object = new JSONObject(result);
                String flag = object.getString("error");
                if (flag.equals("ok")) {
                    String body = object.getString("object");
                    userInfo = gson.fromJson(body, UserInfo.class);
                    handler.sendEmptyMessage(ConstantUtils.LOGIN_GET_DATA);
                } else {
                    showToast("用户名或密码错误！");

                }
            }
        });
    }
    long exitTime = 0;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
            if((System.currentTimeMillis()-exitTime) > 2000){
                Toast.makeText(this,"再按一次退出程序",Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    public static void actionStart(Context context){
        Intent intent = new Intent(context,LoginActivity.class);
        context.startActivity(intent);
    }
    class LoginHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case ConstantUtils.LOGIN_GET_DATA:
                    SharePreferencesUtil.saveObject(context, ConstantUtils.USER_LOGIN_INFO, userInfo);
                    MainActivity.actionStart(context);
                    finish();
                    break;
                default:
                    break;
            }
        }
    }

}
