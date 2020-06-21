package com.example.kiylx.ti.mvp.presenter;


import android.util.Log;

import com.example.kiylx.ti.mvp.model.WebPage_Info;
import com.example.kiylx.ti.tool.dateProcess.TimeProcess;
import com.example.kiylx.ti.mvp.presenter.WebViewManager.Action;
import com.example.kiylx.ti.mvp.model.SealedWebPageInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * 此类用来存储浏览网页产生的网页信息，基本单位是Webpage_info是实时从webview中抽取信息，存储。
 * 用来展示多窗口的网页的列表，当前打开的所有网页信息
 */
public class WebViewInfo_Manager implements Observer {

    private static List<WebPage_Info> mCurrectList;
    private Observable mObservable;
    private static WebViewInfo_Manager s_webViewInfoManager;
    private static final String TAG = "WebViewInfo_Manager";

    private WebViewInfo_Manager() {
        mCurrectList = new ArrayList<>();
    }

    private WebViewInfo_Manager(Observable o) {
        mCurrectList = new ArrayList<>();
        this.mObservable = o;
        mObservable.addObserver(this);
    }

    public static WebViewInfo_Manager get() {
        if (s_webViewInfoManager == null) {
            s_webViewInfoManager = new WebViewInfo_Manager();
        }
        return s_webViewInfoManager;
    }

    public static WebViewInfo_Manager get(Observable o) {
        if (s_webViewInfoManager == null) {
            s_webViewInfoManager = new WebViewInfo_Manager(o);
        }
        return s_webViewInfoManager;
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

    public static List<WebPage_Info> getPageList() throws NullPointerException{
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
            case NEWTAB:
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
    }

    /**
     * @param current 位置
     * @return 返回该位置的info的progress
     */
    public int getProgress(int current) {
        return mCurrectList.get(current).getProgress();
    }
}
