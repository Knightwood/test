package com.example.kiylx.ti.core1;


import android.util.Log;

import com.example.kiylx.ti.corebase.WebPage_Info;
import com.example.kiylx.ti.dateProcess.TimeProcess;
import com.example.kiylx.ti.model.Action;
import com.example.kiylx.ti.corebase.SealedWebPageInfo;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

/**
 * 此类用来存储浏览网页产生的网页信息，基本单位是Webpage_info是实时从webview中抽取信息，存储。
 * 用来展示多窗口的网页的列表，当前打开的所有网页信息
 */
public class Converted_WebPage_Lists implements Observer {

    private static ArrayList<WebPage_Info> mCurrectList;
    private Observable mObservable;
    private static Converted_WebPage_Lists sConverted_webPage_lists;
    private static final String TAG = "Converted_WebPage_Lists";

    private Converted_WebPage_Lists() {
        mCurrectList = new ArrayList<>();
    }

    private Converted_WebPage_Lists(Observable o) {
        mCurrectList = new ArrayList<>();
        this.mObservable = o;
        mObservable.addObserver(this);
    }

    public static Converted_WebPage_Lists get() {
        if (sConverted_webPage_lists == null) {
            sConverted_webPage_lists = new Converted_WebPage_Lists();
        }
        return sConverted_webPage_lists;
    }

    public static Converted_WebPage_Lists get(Observable o) {
        if (sConverted_webPage_lists == null) {
            sConverted_webPage_lists = new Converted_WebPage_Lists(o);
        }
        return sConverted_webPage_lists;
    }

    /**
     * @param pos  要添加到的位置
     * @param info 传入的webPage_info
     */
    private void addToList(int pos, WebPage_Info info) {
        mCurrectList.add(pos, new WebPage_Info(info.getTitle(), info.getUrl(), null, info.getWEB_feature(), info.getDate()));
    }

    public void delete(int index) {
        mCurrectList.remove(index);
    }

    public static ArrayList<WebPage_Info> getPageList() {
        try {
            return mCurrectList;
        } catch (NullPointerException m) {
            m.printStackTrace();
        }
        return mCurrectList;
    }

    public int getPosition(WebPage_Info item) {
        return mCurrectList.indexOf(item);
    }

    public String getTitle(int pos) {
        return mCurrectList.get(pos).getTitle();
    }

    public void setTitle(int pos, String s) {
        mCurrectList.get(pos).setTitle(s);
    }

    public String getUrl(int pos) {
        return mCurrectList.get(pos).getUrl();
    }

    public void setUrl(int pos, String url) {
        mCurrectList.get(pos).setUrl(url);
    }

    /*public void setWEB_feature_1(int pos, int i) {
        mCurrectList.get(pos).setWEB_feature(i);
    }*/

    public WebPage_Info getInfo(int i) {
        return mCurrectList.get(i);
    }

    public String getdate(int i) {
        return mCurrectList.get(i).getDate();
    }

    public void setdate(int i) {
        mCurrectList.get(i).setDate(TimeProcess.getTime());
    }

    @Override
    public void update(Observable o, Object arg) {
        if (arg instanceof SealedWebPageInfo) {
            updateItem(((SealedWebPageInfo) arg).getInfo(), ((SealedWebPageInfo) arg).getPos(), ((SealedWebPageInfo) arg).getAction());
        }

    }

    /**
     * @param info   WebPageInfo对象，被封装的基础
     * @param pos    info在ArrayList中的位置
     * @param action 需要执行的动作，添加，删除或是更新信息
     *               根据action执行相应的更新动作
     */
    private void updateItem(WebPage_Info info, int pos, Action action) {
        switch (action) {
            case ADD:
                addToList(pos, info);
                Log.d(TAG, "updateItem: 添加后数量" + mCurrectList.size());
                break;
            case DELETE:
                delete(pos);
                break;
            case UPDATEINFO:
                for (WebPage_Info hi : mCurrectList) {
                    Log.d(TAG, "更新网页前: " + pos);
                }
                updateinfo(info, pos);
                break;
        }
        for (WebPage_Info hi : mCurrectList) {
            Log.d(TAG, "更新网页时: " + hi.getUrl() + "***" + hi.getTitle());
        }
    }

    private void updateinfo(WebPage_Info info, int pos) {
        WebPage_Info minfo = null;

        minfo = mCurrectList.get(pos);
        minfo.setTitle(info.getTitle());
        minfo.setUrl(info.getUrl());
        minfo.setDate(info.getDate());
        //minfo.setWEB_feature(info.getWEB_feature());
    }
}
