package com.example.kiylx.ti.model;

import android.webkit.WebView;

import java.util.ArrayList;
import java.util.Observable;

/*Clist用来存储webview，管理webview。
    当webview发生更新，包括添加或是删除或是webview自身发生更新，
    使用观察者模式更新Converted_WebPage_Lists中的数据*/
public class WebViewManager extends Observable {

    //存着当前打开的所有webview对象
    private ArrayList<WebView> mArrayList;
    //存储当前使用的webview的网址等数据，用来向观察者推送更新。
    private WebPage_Info tmpData;

    private volatile static WebViewManager sWebViewManager;

    private WebViewManager() {
        if (mArrayList == null) {
            mArrayList = new ArrayList<WebView>();
            tmpData = new WebPage_Info(null,null,null);
        }
    }

    public static WebViewManager getInstance() {
        if (sWebViewManager == null) {
            synchronized (WebViewManager.class) {
                if (sWebViewManager == null) {
                    sWebViewManager = new WebViewManager();
                }
            }
        }
        return sWebViewManager;
    }

    public void addToWebManager(WebView v, int i) {
        //添加到第一个位置，但是也可以指定i的值添加到其他位置
        insertWebView(v, i);
        //更新信息
        setTmpData(v);
    }

    private void insertWebView(WebView v, int i) {
        mArrayList.add(i, v);
        setChanged();
        notifyObservers(getTmpData());
    }

    public void delete(int i) {
        removeWebView(i);
    }

    private void removeWebView(int i) {
        this.mArrayList.remove(i);
        setChanged();
        notifyObservers(1);
    }

    public int size() {
        return this.mArrayList.size();
    }

    public WebView getTop(int i) {
        //返回第一个元素，但是也可以指定i的值获取其他位置的元素
        return mArrayList.get(i);
    }

    public void destroy(int pos) {
        removeWeb(pos);

    }

    private void setTmpData(WebView webView){
        tmpData.setTitle(webView.getTitle());
        tmpData.setUrl(webView.getUrl());
        tmpData.setDate(TimeProcess.getTime());
        tmpData.setFlags(1);

    }
    private WebPage_Info getTmpData(){
        return tmpData;
    }

    public boolean isempty() {
        return mArrayList.isEmpty();
    }

    private void removeWeb(int pos) {
        WebView tmp = mArrayList.get(pos);
        if (tmp != null) {
            //先加载空内容
            tmp.setWebViewClient(null);
            tmp.setWebChromeClient(null);
            tmp.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            //清空历史
            tmp.clearHistory();
            //然后销毁
            tmp.destroy();
            //然后置为空
            mArrayList.remove(pos);
        }
    }

    public void stop(int i) {
        mArrayList.get(i).onPause();
    }

    public void reStart(int i) {
        mArrayList.get(i).onResume();
    }

}
