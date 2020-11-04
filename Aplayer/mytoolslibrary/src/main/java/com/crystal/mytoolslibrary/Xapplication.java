package com.crystal.fucktoollibrary.tools;

import android.app.Application;
import android.content.Context;

import com.crystal.fucktoollibrary.tools.threadpool.SimpleThreadPool;

/**
 * 创建者 kiylx
 * 创建时间 2020/9/9 22:36
 * packageName：com.crystal.myplayer
 * 描述：
 */
public class Xapplication extends Application {
    private Context mContext;
    private SimpleThreadPool threadPool;


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
        threadPool= SimpleThreadPool.getInstance();

    }

    public Context getInstance() {
        return mContext;
    }

    public SimpleThreadPool getThreadPool(){
        return threadPool;
    }
}
