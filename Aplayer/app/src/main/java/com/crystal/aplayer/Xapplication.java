package com.crystal.aplayer;

import android.content.Context;

import com.crystal.fucktoollibrary.tools.Xapplication;


/**
 * 创建者 kiylx
 * 创建时间 2020/9/9 22:36
 * packageName：com.crystal.myplayer
 * 描述：
 */
public class Tapplication extends Xapplication {
    private Context mContext;

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

    }

    @Override
    public Context getInstance() {
        return mContext;
    }
}
