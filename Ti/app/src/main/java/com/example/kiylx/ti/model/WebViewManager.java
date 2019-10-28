package com.example.kiylx.ti.model;

import android.webkit.WebView;

import java.util.ArrayList;
import java.util.Observable;

/**
 * WebViewManager用来存储webview，管理webview。当webview发生更新，包括添加或是删除或是webview自身发生更新，
 * 使用观察者模式更新Converted_WebPage_Lists中的数据。
 * Converted_WebPage_Lists中的抽取出的特定信息的webviewpageinfo和WebViewManager中的webview是一一对应的；webview更新就要用观察者模式更新Converted_WebPage_Lists
 * 通知更新时，数字表示删除的元素位置，webviewpageinfo类型则表示要添加进去。
 */
public class WebViewManager extends Observable {

    //存着当前打开的所有webview对象
    private ArrayList<WebView> mArrayList;
    //存储当前使用的webview的网址等数据，用来向观察者推送更新。
    private WebPage_Info tmpData;

    private volatile static WebViewManager sWebViewManager;

    private WebViewManager() {
        if (mArrayList == null) {
            mArrayList = new ArrayList<WebView>();
            tmpData = new WebPage_Info(null, null, null);
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

    /**
     * @param v    要添加的webview
     * @param i    添加到第一个位置，但是也可以指定i的值添加到其他位置
     * @param flag 标识这是什么网页，-1表示这是新标签页
     *             添加新标签页时，会用另一个版本的setTmpData设置新标签页的信息
     *             然后调用updateWebView()时，传入null，这样tmpData就不会被再次更新
     */
    public void addToWebManager(WebView v, int i, int flag) {
        if (flag == -1) {
            //一个新的空白的webview，title是“空白页”，url是“about:newTab”,flags是“未分类”
            //把网页信息保存进去，flags记为0，表示是一个newTab，不计入历史记录
            setTmpData("空白页", "about:newTab");
            updateWebview(null, 0, Action.ADD);

        } else
            insertWebView(v, i);

    }

    private void insertWebView(WebView v, int i) {
        mArrayList.add(i, v);
        updateWebview(v, i, Action.ADD);
    }

    @Override
    protected synchronized void setChanged() {
        super.setChanged();
    }

    /**
     * @param i 将要删除的元素位置
     */
    public void delete(int i) {
        removeWebView(i);
    }

    private void removeWebView(int i) {
        this.mArrayList.remove(i);
        updateWebview(null, i, Action.DELETE);
    }

    public int size() {
        return this.mArrayList.size();
    }

    public WebView getTop(int i) {
        //返回第一个元素，但是也可以指定i的值获取其他位置的元素
        return mArrayList.get(i);
    }


    /**
     * @param title 网址标题
     * @param url   URL
     *              这是为生成新标签页准备的方法。
     */
    private void setTmpData(String title, String url) {
        tmpData.setTitle(title);
        tmpData.setUrl(url);
        tmpData.setDate(TimeProcess.getTime());
        tmpData.setFlags(1);
    }

    /**
     * @param webView 传入webview实例，初始化tempData，以备观察者推送更新
     */
    private void setTmpData(WebView webView) {
        if (webView == null) {
            return;
        }
        tmpData.setTitle(webView.getTitle());
        tmpData.setUrl(webView.getUrl());
        tmpData.setDate(TimeProcess.getTime());
        tmpData.setFlags(1);

    }

    /**
     * @param pos    tpmDate指向的WebView在lists中的位置
     * @param action 动作：添加，删除或是更新数据
     * @return 获得SealedWebPageInfo
     * 获取封装好的WebPageInfo，后面将用它作为推送给观察者的数据
     */
    private SealedWebPageInfo getSealedData(int pos, Action action) {
        return new SealedWebPageInfo(tmpData, pos, action);
    }

    /**
     * @param pos WebView在list中的位置。
     *            当网页载入了新的网址，WebView会更新，
     *            所以，当WebView更新时，就要相应的更新Converted_WebPage_Lists中相应的条目信息
     */
    public void notifyWebViewUpdate(int pos) {
        updateWebview(mArrayList.get(pos), pos, Action.UPDATEINFO);
    }

    /**
     * @param arg    发生变化的Webview
     * @param i      webview在arraylist中的位置。
     * @param action 要执行的动作：添加，删除，或是更新
     *               网页载入了网址，要触发这个方法，更新Convented_WebviewPage_List网页信息.
     */
    public void updateWebview(WebView arg, int i, Action action) {
        //用传入的webview更新tmpData，后面需要用tmp进行封装
        setTmpData(arg);
        setChanged();
        //用封装的WebPageInfo执行推送
        notifyObservers(getSealedData(i, action));

    }

    public boolean isempty() {
        return mArrayList.isEmpty();
    }

    public void destroy(int pos) {
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
            delete(pos);
        }
    }

    public void stop(int i) {
        mArrayList.get(i).onPause();
    }

    public void reStart(int i) {
        mArrayList.get(i).onResume();
    }

}
