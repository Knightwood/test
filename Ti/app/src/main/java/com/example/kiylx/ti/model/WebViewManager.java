package com.example.kiylx.ti.model;

import android.content.Context;
import android.util.Log;
import android.webkit.WebView;

import com.example.kiylx.ti.AboutHistory;
import com.example.kiylx.ti.Activitys.MainActivity;
import com.example.kiylx.ti.DateProcess.TimeProcess;
import com.example.kiylx.ti.INTERFACE.NotifyWebViewUpdate;

import java.util.ArrayList;
import java.util.Observable;

/**
 * WebViewManager用来存储webview，管理webview。当webview发生更新，包括添加或是删除或是webview自身发生更新，
 * 使用观察者模式更新Converted_WebPage_Lists中的数据。
 * Converted_WebPage_Lists中的抽取出的特定信息的webviewpageinfo和WebViewManager中的webview是一一对应的；webview更新就要用观察者模式更新Converted_WebPage_Lists
 * 通知更新时，数字表示删除的元素位置，webviewpageinfo类型则表示要添加进去。
 */
public class WebViewManager extends Observable implements NotifyWebViewUpdate {

    //存着当前打开的所有webview对象
    private ArrayList<WebView> mArrayList;
    //存储当前使用的webview的网址等数据，用来向观察者推送更新。
    private WebPage_Info tmpData;
    private volatile static WebViewManager sWebViewManager;
    private AboutHistory sAboutHistory;
    private Context mContext;
    private static final String TAG = "WebViewManager";

    private WebViewManager(Context context) {
        if (mArrayList == null) {
            mArrayList = new ArrayList<WebView>();
            tmpData = new WebPage_Info(null, null, null, 0, null);
            this.mContext = context;
        }
    }

    public static WebViewManager getInstance(Context context) {
        if (sWebViewManager == null) {
            synchronized (WebViewManager.class) {
                if (sWebViewManager == null) {
                    sWebViewManager = new WebViewManager(context);
                    //传入实现了接口的实例变量
                    //CustomWebviewClient.setInterface(sWebViewManager);
                    CustomWebchromeClient.setInterface(sWebViewManager);
                }
            }
        }
        return sWebViewManager;
    }

    /**
     * @param v    要添加的webview
     * @param i    添加到第一个位置，但是也可以指定i的值添加到其他位置
     * @param flag 标识这是什么网页，0表示这是新标签页
     */
    public void addToWebManager(WebView v, int i, int flag) {
        if (flag == 0) {
            //一个新的空白的webview，title是“空白页”，url是“about:newTab”,flags是“未分类”
            //把网页信息保存进去，flags记为0，表示是一个newTab，不计入历史记录
            insertWebView(v, i, 0);
        } else {
            insertWebView(v, i, 1);
        }

    }

    /**
     * @param v    要添加进mArrayList的webview
     * @param i    要添加到的位置
     * @param flag 如果是0，标识这是新标签页，执行特定操作
     */
    private void insertWebView(WebView v, int i, int flag) {
        mArrayList.add(i, v);
        if (flag == 0) {
            notifyupdate(null, i, Action.ADD);
        } else
            notifyupdate(v, i, Action.ADD);
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
        notifyupdate(null, i, Action.DELETE);
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
        tmpData.setWEB_feature(0);
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
        tmpData.setWEB_feature(1);

    }

    /**
     * @param pos    tpmDate指向的WebView在lists中的位置,也就是即将加入到Converted_WebPage_Lists中的位置
     * @param action 动作：添加，删除或是更新数据
     * @return 获得SealedWebPageInfo
     * 获取封装好的WebPageInfo，后面将用它作为推送给观察者的数据
     */
    private SealedWebPageInfo getSealedData(int pos, Action action) {
        Log.d(TAG, "getSealedData: 添加数据");
        return new SealedWebPageInfo(tmpData, pos, action);
    }


    /**
     * @param webView 传入webview，
     *                然后在方法里找到WebView在list中的位置，再进行更新操作，
     *                这样，就可以保证在切换webview时保证更新不会出错
     *                且，这个方法是在网页加载完成时调用的，所以，只要判断不是在用户界面上的webview，就可以调用暂停webview以节省性能
     *                <p>
     *                <p>
     *                网页加载完成时调用的更新方法！！！
     *                当WebView更新时，就要相应的更新Converted_WebPage_Lists中相应的条目信息
     */
    @Override
    public void notifyWebViewUpdate(WebView webView) {

        /*
        遍历所有进行更新
        for(int pos=0;pos<mArrayList.size();pos++){
            notifyupdate(mArrayList.get(pos), pos, Action.UPDATEINFO);
            Log.d(TAG, "notifyWebViewUpdate: "+mArrayList.get(pos).getTitle());
            if (MainActivity.getCurrect() != pos) {
                stop(pos);
            }
        }*/

        int pos = mArrayList.indexOf(webView);
        notifyupdate(mArrayList.get(pos), pos, Action.UPDATEINFO);
        if (MainActivity.getCurrect() != pos) {
            stop(pos);
        }

        for (int p = 0; p < mArrayList.size(); p++) {
            Log.d(TAG, "notifyWebViewUpdate: " + mArrayList.get(p).getUrl());

        }


    }

    /**
     * @param arg    发生变化的Webview
     * @param i      webview在arraylist中的位置。
     * @param action 要执行的动作：添加，删除，或是更新
     *               <p>
     *               如果webview传入的是null，那意味着这是一个新标签页，调用另一版本setData
     *               观察者模式的更新操作！！！
     *               网页载入了网址，触发观察者模式，这个方法，更新Convented_WebviewPage_List网页信息.
     *               并且，把被更新的网页信息加入历史记录数据库
     */
    public void notifyupdate(WebView arg, int i, Action action) {
        //用传入的webview更新tmpData，后面需要用tmp进行封装
        if (action == Action.ADD) {
            setTmpData("空白页", "about:newTab");
        } else if (action == Action.DELETE) {
            setTmpData(null);
        } else {
            setTmpData(arg);
        }

        setChanged();
        //用封装的WebPageInfo执行推送
        notifyObservers(getSealedData(i, action));
        //如果不是新标签页就加入数据库
        if (tmpData.getWEB_feature()!=0) {
            //历史记录加入数据库
            sAboutHistory = AboutHistory.get(mContext);
            sAboutHistory.addToDataBase(tmpData);
        }

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
