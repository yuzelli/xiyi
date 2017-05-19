package com.example.yuzelli.yiai.view.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.widget.ImageView;


import com.example.yuzelli.yiai.R;
import com.example.yuzelli.yiai.base.BaseActivity;
import com.example.yuzelli.yiai.bean.UserInfo;
import com.example.yuzelli.yiai.constants.ConstantUtils;
import com.example.yuzelli.yiai.uitls.SharePreferencesUtil;

import java.util.Random;


/**
 * 闪屏页，判断用户是否第一次登录
 */
public class SplashActivity extends BaseActivity {
    private boolean firstUse;
    private Context context;
    private ImageView iv_spl_background;
    private static final int ANIMATION_DURATION = 3000;
    private static final float SCALE_END = 1.13F;
    private static final int[] SPLASH_ARRAY = {
            R.drawable.splash0,
            R.drawable.splash1,
            R.drawable.splash2,
            R.drawable.splash3,
            R.drawable.splash4,
            R.drawable.splash5,
            R.drawable.splash6,
            R.drawable.splash7,
            R.drawable.splash8,
            R.drawable.splash9,
            R.drawable.splash10,
            R.drawable.splash11,
            R.drawable.splash12,
            R.drawable.splash13,
            R.drawable.splash14,
            R.drawable.splash15,
            R.drawable.splash16,
    };

    @Override
    protected int layoutInit() {
        return R.layout.activity_splash;
    }

    @Override
    protected void binEvent() {
        iv_spl_background = (ImageView) this.findViewById(R.id.iv_spl_background);
        Random r = new Random(SystemClock.elapsedRealtime());
        iv_spl_background.setImageResource(SPLASH_ARRAY[r.nextInt(SPLASH_ARRAY.length)]);
        context = SplashActivity.this;
        firstUse = getSharedPreferences(ConstantUtils.FIRST_LOGIN_SP,MODE_PRIVATE).getBoolean(ConstantUtils.FIRST_LOGIN_KEY,true);
        animateImage();
    }

    private void animateImage() {
        ObjectAnimator animatorX = ObjectAnimator.ofFloat(iv_spl_background, "scaleX", 1f, SCALE_END);
        ObjectAnimator animatorY = ObjectAnimator.ofFloat(iv_spl_background, "scaleY", 1f, SCALE_END);

        AnimatorSet set = new AnimatorSet();
        set.setDuration(ANIMATION_DURATION).play(animatorX).with(animatorY);
        set.start();

        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                // handler.sendEmptyMessageDelayed(ConstantUtil.START_ACTIVITY, 3000);
                handler.sendEmptyMessage(ConstantUtils.SPLASH_START_ACTIVITY);
            }
        });
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case ConstantUtils.SPLASH_START_ACTIVITY:
                    if (!firstUse) {
                        UserInfo userInfo = (UserInfo) SharePreferencesUtil.readObject(context,ConstantUtils.USER_LOGIN_INFO);
                        if (userInfo!=null) {
                            MainActivity.actionStart(context);
                        }else {
                            LoginActivity.actionStart(context);
                        }
                    } else {
                        startActivity(new Intent(SplashActivity.this, GuideActivity.class));
                    }
                    finish();
                    break;
            }
        }
    };


}