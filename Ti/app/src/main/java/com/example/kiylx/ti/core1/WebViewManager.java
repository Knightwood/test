package com.example.kiylx.ti.core1;

import android.content.Context;
import android.util.Log;
import android.webkit.WebView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.kiylx.ti.R;
import com.example.kiylx.ti.activitys.MainActivity;
import com.example.kiylx.ti.corebase.WebPage_Info;
import com.example.kiylx.ti.dateProcess.TimeProcess;
import com.example.kiylx.ti.myInterface.HistoryInterface;
import com.example.kiylx.ti.myInterface.NotifyWebViewUpdate;
import com.example.kiylx.ti.model.Action;
import com.example.kiylx.ti.corebase.SealedWebPageInfo;

import java.util.ArrayList;
import java.util.Observable;

/**
 * WebViewManager用来存储webview对象，管理webview。当webview发生更新，包括添加或是删除或是webview自身发生更新，
 * 使用观察者模式更新Converted_WebPage_Lists中的数据。
 * Converted_WebPage_Lists中的抽取出的特定信息的webviewpageinfo和WebViewManager中的webview是一一对应的；webview更新就要用观察者模式更新Converted_WebPage_Lists
 * 通知更新时，数字表示删除的元素位置，webviewpageinfo类型则表示要添加进去。
 */
public class WebViewManager extends Observable implements NotifyWebViewUpdate {

    //存着当前打开的所有webview对象
    private ArrayList<WebView> webViewArrayList;
    //存储当前使用的webview的网址等数据，用来向观察者推送更新。
    private WebPage_Info tmpData;
    private volatile static WebViewManager sWebViewManager;
    private static final String TAG = "WebViewManager";
    private HistoryInterface m_historyInterface;

    private WebViewManager(Context context) {
        m_historyInterface = AboutHistory.get(context);

        if (webViewArrayList == null) {
            webViewArrayList = new ArrayList<>();
            tmpData = new WebPage_Info(null, null, null, 0, null);
        }
    }

    public static WebViewManager getInstance(Context context) {
        if (sWebViewManager == null) {
            synchronized (WebViewManager.class) {
                if (sWebViewManager == null) {
                    sWebViewManager = new WebViewManager(context);
                    //传入实现了接口的实例变量

                    CustomWebchromeClient.setInterface(sWebViewManager);
                }
            }
        }
        return sWebViewManager;
    }

    /**
     * @param i pos，webview要添加进的位置，默认传0
     *          新建一个webview并放进WebViewManager（webview2类型）
     */
    public void newWebView(int i, Context applicationContext, AppCompatActivity appCompatActivity) {
        //新建webview并放进数组

//注：new一个webview
        CustomActionWebView web = new CustomActionWebView(applicationContext);

        web.setActionList();//点击浏览webview的菜单项

        WebiVewSetting.set1(web, appCompatActivity);
        //给new出来的webview执行设置
        web.setWebViewClient(new CustomWebviewClient(appCompatActivity));
        web.setWebChromeClient(new CustomWebchromeClient());

        //添加js，用来展开菜单的方法。
        web.MenuJSInterface();

        this.addInWebManager(web, i);

    }

    /**
     * @param v 要添加的webview
     * @param i 添加到第一个位置，但是也可以指定i的值添加到其他位置
     */
    public void addInWebManager(WebView v, int i) {
        insert_12(v, i);

    }

    /**
     * @param v 要添加进mArrayList的webview
     * @param i 要添加到的位置
     */
    private void insert_12(WebView v, int i) {
        webViewArrayList.add(i, v);
        notifyupdate(v, i, Action.ADD);
    }

    /* *//**
     * @param v    要添加的webview
     * @param i    添加到第一个位置，但是也可以指定i的值添加到其他位置
     * @param web_feature 标识这是什么网页，0表示这是新标签页
     *//*
    public void addInWebManager(WebView v, int i, int web_feature) {
        //一个新的空白的webview，title是“空白页”，url是“about:newTab”,flags是“未分类”
        //把网页信息保存进去，web_feature记为0，表示是一个新标签页，不计入历史记录
        insert_1(v, i, web_feature == 0 ? 0 : 1);

    }

    *//**
     * @param v    要添加进mArrayList的webview
     * @param i    要添加到的位置
     * @param web_feature 如果是0，标识这是新标签页，执行特定操作
     *//*
    private void insert_1(WebView v, int i, int web_feature) {
        webViewArrayList.add(i, v);
        if (web_feature == 0) {
            notifyupdate(null, i, Action.ADD);
        } else
            notifyupdate(v, i, Action.ADD);
    }*/

    /**
     * 标记观察者更新
     * <p>
     * 还可以处理更多的东西，但我没写。
     */
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
        this.webViewArrayList.remove(i);
        notifyupdate(null, i, Action.DELETE);
    }

    /**
     * @return 返回存储webview对象的list的大小
     */
    public int size() {
        return this.webViewArrayList.size();
    }

    /**
     * @param pos webview位置，如果传入的是-1，让0位置的webview加载主页，否则按照pos位置的webview加载主页
     * 载入主页
     */
    public void loadHomePage(int pos) {
            getTop(pos ==-1? 0:pos).loadUrl(String.valueOf (R.string.default_homePage_url));
            String uii=String.valueOf(R.string.default_homePage_url);
        Log.d(TAG, "loadHomePage: "+uii);
    }

    /**
     * @param i webview列表的指定位置，默认传入0
     * @return 列表中的webview(转制为CustomActionWebView类型)
     * <p>
     * 这个方法是返回自定义的webview子类类型。
     */
    public CustomActionWebView getTop(int i) {
        return (CustomActionWebView) webViewArrayList.get(i);
    }

    /**
     * @param i webview列表的指定位置，默认传入0
     * @return 列表中的webview
     */
    public WebView getTop1(int i) {
        return webViewArrayList.get(i);
    }


    /**
     * @param title 网址标题
     * @param url   URL
     *              这是为生成新标签页准备的方法。
     */
    private void setTmpData_newPage(String title, String url) {
        tmpData.setTitle(title);
        tmpData.setUrl(url);

        tmpData.setDate(TimeProcess.getTime());
        //tmpData.setWEB_feature(0);
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
        //tmpData.setWEB_feature(1);

    }

    /**
     * @param pos    tmpDate指向的WebView在lists中的位置,也就是即将加入到Converted_WebPage_Lists中的位置
     * @param action 动作：添加，删除或是更新数据
     * @return 获得SealedWebPageInfo
     * 获取封装好的WebPageInfo，后面将用它作为推送给观察者的数据
     */
    private SealedWebPageInfo getSealedData(int pos, Action action) {
        Log.d(TAG, "getSealedData: 添加数据");
        return new SealedWebPageInfo(tmpData, pos, action);
    }


    /**
     * 网页加载完成时会调用他更新网页信息
     *
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
    //方1，遍历所有，进行更新
        for(int pos=0;pos<webViewArrayList.size();pos++){
            notifyupdate(webViewArrayList.get(pos), pos, Action.UPDATEINFO);
            Log.d(TAG, "notifyWebViewUpdate: "+webViewArrayList.get(pos).getTitle());
            if (MainActivity.getCurrect() != pos) {
                stop(pos);
            }
        }*/
        //方2
        int pos = webViewArrayList.indexOf(webView);
        notifyupdate(webViewArrayList.get(pos), pos, Action.UPDATEINFO);
        if (MainActivity.getCurrect() != pos) {
            stop(pos);
        }

        for (int p = 0; p < webViewArrayList.size(); p++) {
            Log.d(TAG, "notifyWebViewUpdate: " + webViewArrayList.get(p).getUrl());

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
    private void notifyupdate(WebView arg, int i, Action action) {
        //用传入的webview更新tmpData，后面需要用tmp进行封装
        if (action == Action.ADD) {
            //添加，添加的新标签页
            setTmpData_newPage(String.valueOf(R.string.homePage), String.valueOf(R.string.default_homePage_url));
        } else if (action == Action.DELETE) {
            //删除
            setTmpData(null);
        } else {
            //更新
            setTmpData(arg);
        }

        setChanged();
        //用封装的WebPageInfo执行推送
        notifyObservers(getSealedData(i, action));
        //如果不是新标签页就加入数据库
        if (! tmpData.getUrl().equals(String.valueOf(R.string.default_homePage_url) )) {//if里原来是tmpData.getWEB_feature() != 0
            //历史记录加入数据库
            m_historyInterface.addData(tmpData);

        }

    }

    public boolean isempty() {
        return webViewArrayList.isEmpty();
    }

    public void destroy(int pos) {
        WebView tmp = webViewArrayList.get(pos);
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
        webViewArrayList.get(i).onPause();
    }

    public void reStart(int i) {
        webViewArrayList.get(i).onResume();
    }

}
