package com.example.yuzelli.yiai.view.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Request;


public class RegisterActivity extends BaseActivity {

    private EditText et_phone;
    private EditText et_password;
    private EditText et_ok_password;

    private TextView tv_register;

    private ImageView cb_agree;
    private boolean agreeFlag = true;
    private Context context;
    private RegisterHandler handler;
    private UserInfo userInfo;

    @Override
    protected int layoutInit() {
        return R.layout.activity_register;
    }

    @Override
    protected void binEvent() {
        findViewById(R.id.img_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        handler = new RegisterHandler();
        context = this;
        et_phone = (EditText) this.findViewById(R.id.et_phone);
        et_password = (EditText) this.findViewById(R.id.et_password);
        et_ok_password = (EditText) this.findViewById(R.id.et_ok_password);

        tv_register = (TextView) this.findViewById(R.id.tv_register);
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
        tv_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (agreeFlag) {
                    doRegisterAction();
                } else {
                    Toast.makeText(context, "请同意协议", Toast.LENGTH_SHORT).show();
                }
            }
        });
        TextView tv_title = (TextView) this.findViewById(R.id.tv_title);

    }

    private void doRegisterAction() {
        String phone = et_phone.getText().toString().trim();
        String password = et_password.getText().toString().trim();
        String ok_password = et_ok_password.getText().toString().trim();

        if (phone == null || phone.equals("")) {
            Toast.makeText(context, "请输入手机号", Toast.LENGTH_SHORT).show();
        }
        if (password == null || password.equals("")) {
            Toast.makeText(context, "请输入密码", Toast.LENGTH_SHORT).show();
        }
        if (ok_password == null || ok_password.equals("")) {
            Toast.makeText(context, "请确认密码", Toast.LENGTH_SHORT).show();
        }
        if (!ok_password.equals(password)) {
            Toast.makeText(context, "密码不一致", Toast.LENGTH_SHORT).show();
        }

            if (LoginUtils.isPhoneEnable(phone)) {
               doRegister(phone,password);
            } else {
                Toast.makeText(context, "输入手机号错误", Toast.LENGTH_SHORT).show();
            }


    }

    private void doRegister(String userPhone,String passWord) {
        OkHttpClientManager manager = OkHttpClientManager.getInstance();
        StringBuffer buffer = new StringBuffer(ConstantUtils.USER_ADDRESS).append(ConstantUtils.USER_REGISTER);
        Map<String, String> map = new HashMap<>();
        map.put("type", "register");
        map.put("u_phone", userPhone);
        map.put("u_password", passWord);
        map.put("u_imgurl", "");
        map.put("u_address", "未设置");
        map.put("u_name", "未设置");
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
                    handler.sendEmptyMessage(ConstantUtils.REGISTER_GET_DATA);
                } else {
                    showToast("用户名或密码错误！");
                }
            }
        });
    }

    class RegisterHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case ConstantUtils.REGISTER_GET_DATA:
                    SharePreferencesUtil.saveObject(context, ConstantUtils.USER_LOGIN_INFO, userInfo);
                    finish();
                    break;
                default:
                    break;
            }
        }
    }
    public static void actionStart(Context context) {
        Intent intent = new Intent(context, RegisterActivity.class);
        context.startActivity(intent);
    }
}
