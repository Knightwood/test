package com.example.kiylx.ti;

import android.app.Application;
import android.content.Context;
import android.preference.PreferenceManager;

import com.example.kiylx.ti.tool.DefaultPreferenceTool;
import com.example.kiylx.ti.tool.ShowPicMode;

/**
 * 创建者 kiylx
 * 创建时间 2020/5/6 11:09
 */
public class Xapplication extends Application {
    private static Context mContext;

    private ShowPicMode showPicture;//是否显示网页图片，1：禁止显示图片；2：始终显示图片；3：在wifi下显示图片
    private boolean isPrivacy;//是否使用了隐私模式



    @Override
    public void onCreate() {
        super.onCreate();
        mContext=this;

        initPreference();
        initData();
    }

    /**
     * 初始化全局数据
     */
    private void initData() {

    }

    /**
     * 初始化全局preference数据
     */
    private void initPreference() {

    }


    public static Context getInstance(){
        return mContext;
    }

    public ShowPicMode getShowPicture() {
        return showPicture;
    }

    public void setShowPicture(ShowPicMode showPicture) {
        this.showPicture = showPicture;
        PreferenceManager.getDefaultSharedPreferences(mContext).edit().putString("showPicatureMode",showPicture.toString()).apply();
    }

    public boolean isPrivacy() {
        return isPrivacy;
    }

    public void setPrivacy(boolean privacy) {
        isPrivacy = privacy;
        PreferenceManager.getDefaultSharedPreferences(mContext).edit().putBoolean("privacyMode",privacy).apply();
    }


}
