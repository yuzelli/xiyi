package com.example.yuzelli.yiai.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.yuzelli.yiai.uitls.MYToast;


public abstract class BaseFragment extends Fragment {
    protected Context context;
    protected Bundle bundle;
    private View rootView;
    protected abstract int layoutInit();
    protected abstract void bindEvent(View view);
    protected abstract void fillData();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(layoutInit(), container, false);
            viewInit(rootView);
            bindEvent(rootView);
        }
        // 缓存的rootView需要判断是否已经被加过parent，如果有parent需要从parent删除，要不然会发生这个rootview已经有parent的错误。
        ViewGroup parent = (ViewGroup) rootView.getParent();
        if (parent != null) {
            parent.removeView(rootView);
        }
        context = getActivity();

        return rootView;

    }

    protected void viewInit(View v) {

    }

    @Override
    public void onResume() {
        super.onResume();
        fillData();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    public void showToast(String msg) {
        MYToast.show(msg);
    }
}
