package com.example.kiylx.ti;

import android.app.Application;
import android.content.Context;

import com.example.kiylx.ti.mvp.presenter.StateManager;

/**
 * 创建者 kiylx
 * 创建时间 2020/5/6 11:09
 */
public class Xapplication extends Application {
    private static Context mContext;
    private StateManager stateManager;//持有一些状态标志


    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        initData();
    }

    /**
     * 初始化全局数据
     */
    private void initData() {
        stateManager =new StateManager(mContext);

    }
    public static Context getInstance() {
        return mContext;
    }
    public StateManager getStateManager() {
        return stateManager;
    }
}
