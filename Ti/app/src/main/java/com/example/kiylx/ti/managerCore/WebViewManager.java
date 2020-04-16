package com.example.kiylx.ti.managerCore;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.kiylx.ti.ui.activitys.MainActivity;
import com.example.kiylx.ti.Tool.PreferenceTools;
import com.example.kiylx.ti.conf.SomeRes;
import com.example.kiylx.ti.conf.WebviewConf;
import com.example.kiylx.ti.corebase.WebPage_Info;
import com.example.kiylx.ti.Tool.dateProcess.TimeProcess;
import com.example.kiylx.ti.downloadPack.downloadCore.DownloadListener1;
import com.example.kiylx.ti.downloadPack.downloadCore.DownloadListener2;
import com.example.kiylx.ti.Tool.EventMessage;
import com.example.kiylx.ti.myInterface.HandleClickedLinks;
import com.example.kiylx.ti.myInterface.NotifyWebViewUpdate;
import com.example.kiylx.ti.Tool.Action;
import com.example.kiylx.ti.corebase.SealedWebPageInfo;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;

/**
 * WebViewManager用来存储webview对象，管理webview。当webview发生更新，包括添加或是删除或是webview自身发生更新，
 * 使用观察者模式更新Converted_WebPage_Lists中的数据。
 * Converted_WebPage_Lists中的抽取出的特定信息的webviewpageinfo和WebViewManager中的webview是一一对应的；webview更新就要用观察者模式更新Converted_WebPage_Lists
 * 通知更新时，数字表示删除的元素位置，webviewpageinfo类型则表示要添加进去。
 */
public class WebViewManager extends Observable {//implements NotifyWebViewUpdate

    //存着当前打开的所有webview对象
    private List<WebView> webViewArrayList;
    private volatile static WebViewManager sWebViewManager;
    private static final String TAG = "WebViewManager";
    private CustomWebviewClient customWebviewClient;
    private CustomWebchromeClient customWebchromeClient;
    private List<WebView> trashList;//要删除的webview先转移到这里，之后整体删除，避免在原list中操作耗时

    private AboutHistory aboutHistory;
    private HandleClickedLinks mHandleClickedLinks;
    private NotifyWebViewUpdate mUpdateInterface;

    private WebViewManager(Context context, HandleClickedLinks handleClickedLinks) {
        aboutHistory = AboutHistory.get(context);

        if (webViewArrayList == null) {
            webViewArrayList = new ArrayList<>();
            //tmpData = new WebPage_Info(null, null, null, 0, null);
        }
        customWebchromeClient = new CustomWebchromeClient();
        customWebviewClient = new CustomWebviewClient(context);

        mHandleClickedLinks = handleClickedLinks;

        implUpdateWebInfo();
        //传入实现了接口的实例变量
        CustomWebviewClient.setInterface(mUpdateInterface);
        CustomWebchromeClient.setInterface(mUpdateInterface);
    }

    public static WebViewManager getInstance(@NonNull Context context, @NonNull HandleClickedLinks handleClickedLinks) {
        if (sWebViewManager == null) {
            synchronized (WebViewManager.class) {
                if (sWebViewManager == null) {
                    sWebViewManager = new WebViewManager(context, handleClickedLinks);
                }
            }
        }
        return sWebViewManager;
    }

   /* public void setInterface(@NonNull Setmessage interface_1) {
        this.setmessage = interface_1;
    }*/

    /**
     * @param pos pos，webview要添加进的位置
     *            新建一个webview并放进WebViewManager（webview2类型）
     */
    public void newWebView(int pos, Context applicationContext, AppCompatActivity appCompatActivity) {

//注：new一个webview
        CustomAWebView web = new CustomAWebView(applicationContext);
        web.setHandleClickLinks(mHandleClickedLinks);

        web.setActionList();//点击浏览webview的菜单项

        setWebview(web, appCompatActivity);
        //给new出来的webview执行设置
        web.setWebViewClient(customWebviewClient);
        web.setWebChromeClient(customWebchromeClient);
        //添加js，用来展开菜单的方法。
        web.MenuJSInterface();

        addInWebManager(web, pos);

    }

    /**
     * @param v   要添加的webview
     * @param pos 添加到位置，可以指定添加到其他位置
     *            <p>
     *            把webview放入manager管理的list
     */
    public void addInWebManager(WebView v, int pos) {
        insert_12(v, pos);
        //一个新的空白的webview，title是“空白页”，url是“about:newTab”,flags是“未分类”
        Log.d(TAG, "addInWebManager: ");

    }

    /**
     * @param v   要添加进mArrayList的webview
     * @param pos 要添加到的位置
     */
    private void insert_12(WebView v, int pos) {
        webViewArrayList.add(pos, v);
        notifyupdate(v, pos, Action.ADD, true);//更新网页信息的数据
    }

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
     * @param pos webview在list中的位置
     * @param b 让webview恢复加载(在删除webview时，有些webview是处于onStop的。)
     *            <p>
     *            把要删除的webview放进trashList，在合适的时机调用throwTrash()把这些webview全部删除
     */
    public void removeToTrash(int pos,boolean b) {
        if (this.trashList == null) {
            trashList = new ArrayList<>();
        }
        WebView tmp=webViewArrayList.remove(pos);
        tmp.onResume();
        trashList.add(tmp);
        notifyupdate(null, pos, Action.DELETE, false);
    }

    /**
     * 遍历trashList，把里面所有的webview摧毁
     */
    public void throwTrash() {
        if (trashList==null||trashList.isEmpty()) {
            return;
        }
        Iterator iterator = trashList.iterator();
        while (iterator.hasNext()) {
            destroy2((WebView) iterator.next());
        }
        trashList=null;
    }

    /**
     * @param v 要摧毁的webview
     *          <p>
     *          摧毁这个webview。防止内存泄漏
     */
    private void destroy2(WebView v) {
        if (v != null) {
            v.loadUrl("about:blank");
            v.stopLoading();
            v.onPause();
            v.clearSslPreferences();
            v.clearMatches();
            v.clearHistory();
            v.setWebViewClient(null);
            v.setWebChromeClient(null);
            v.removeAllViews();
            //然后销毁
            v.destroy();
        }
    }

    /**
     * @param pos webview在list中的位置
     * @param url 网址（主页网址）
     *
     *            <p>
     *            把pos位置的webview删除历史记录，然后加载url
     */
    public void wash(int pos, String url) {
        WebView tmp;
        tmp = webViewArrayList.get(pos);
        tmp.clearHistory();
        tmp.loadUrl(url);
    }

    /**
     * 刷新网页
     */
    public void reLoad(AppCompatActivity context) {
        setWebview(getTop(MainActivity.getCurrent()), context);
        getTop(MainActivity.getCurrent()).reload();
    }

    /**
     * 使用桌面版的useragent达到访问桌面版的效果
     */
    public void reLoad_pcmode() {
        WebView webView = getTop(MainActivity.getCurrent());
        webView.getSettings().setUserAgentString(SomeRes.PCuserAgent);
        webView.reload();

    }

    /**
     * @return 返回存储webview对象的list的大小
     */
    public int size() {
        return this.webViewArrayList.size();
    }

    /**
     * @param pos webview位置，让pos位置的webview加载网址
     *            废弃： 如果传入的是-1，让0位置的webview加载主页，否则按照pos位置的webview加载主页,新建标签页默认插入0号位置，所以需要传入-1，让新添加的网页载入网址
     * @param url 要载入的主页网址，若是null，载入默认主页
     *            载入主页
     */
    public void loadHomePage(int pos, String url) {
        if (url == null) {
            getTop(pos).loadUrl(SomeRes.default_homePage_url);
        } else {
            getTop(pos).loadUrl(url);
        }

    }

    /**
     * @param i webview列表的指定位置
     * @return 列表中的webview(转制为CustomActionWebView类型)
     * <p>
     * 这个方法是返回自定义的webview子类类型。
     */
    public CustomAWebView getTop(int i) {
        return (CustomAWebView) webViewArrayList.get(i);
    }

    /**
     * @param i webview列表的指定位置
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
    private WebPage_Info setTmpData_newPage(String title, String url) {
        return new WebPage_Info(title, url, null, 0, TimeProcess.getTime());
    }

    /**
     * @param webView 传入webview实例，初始化tempData，以备观察者推送更新
     */
    private WebPage_Info setTmpData(WebView webView) {
        WebPage_Info tmpData = new WebPage_Info(null, null, null, 0, null);

        if (webView == null) {
            return tmpData;
        }
        tmpData.setTitle(webView.getTitle());
        tmpData.setUrl(webView.getUrl());
        tmpData.setDate(TimeProcess.getTime());
        return tmpData;

    }

    /**
     * @param pos    tmpDate指向的WebView在lists中的位置,也就是即将加入到Converted_WebPage_Lists中的位置
     * @param action 动作：添加，删除或是更新数据
     * @return 获得SealedWebPageInfo
     * 获取封装好的WebPageInfo，后面将用它作为推送给观察者的数据
     */
    private SealedWebPageInfo getSealedData(WebPage_Info info, int pos, Action action) {
        Log.d(TAG, "getSealedData: 添加数据");
        return new SealedWebPageInfo(info, pos, action);
    }

    /**
     * 一个方法用于更新数据库
     * 一个方法用于网页加载完成时更新标题
     */
    private void implUpdateWebInfo() {
        if (mUpdateInterface == null)
            mUpdateInterface = new NotifyWebViewUpdate() {
                /**
                 * @param webView webview
                 *                <p>
                 *                在CustomWebviewClient中的doUpdateVisitedHistory方法中调用了该方法
                 *                <p>
                 *                1.更新标题
                 *                2.后台加载时，会在此时停止加载
                 *                3.以及加入数据库
                 *                <p>
                 *                然后在方法里找到WebView在list中的位置，再进行更新操作，
                 *                这样，就可以保证在切换webview时保证更新不会出错
                 *                且，这个方法是在url可见时（doUpdateVisitedHistory()）调用的，所以，只要判断不是在用户界面上的webview，就可以调用暂停webview以节省性能
                 *                <p>
                 *                当WebView更新时，就要相应的更新Converted_WebPage_Lists中相应的条目信息
                 */
                @Override
                public void updateWebViewInfo(WebView webView) {
                    int pos = webViewArrayList.indexOf(webView);
                    notifyupdate(webViewArrayList.get(pos), pos, Action.UPDATEINFO, true);
                    Log.d(TAG, "updateWebViewInfo: pos" + pos + "current" + pos);
                    if (MainActivity.getCurrent() != pos) {
                        stop(pos);//后台加载网页时，current就和这个webview在list中的pos不等。
                    }
                }

                /**
                 * @param webView
                 * 在网页加载到100时在webchromeclient中调用，更新webpageinfo信息，更新多窗口的标题
                 */
                @Override
                public void updateTitle(WebView webView) {
                    int pos = webViewArrayList.indexOf(webView);
                    notifyupdate(webViewArrayList.get(pos), pos, Action.UPDATEINFO, false);
                    notifyTitleUpdate();
                }
            };
    }

    /**
     * 网页加载完成时，通过这里发送事件，
     * mainactivity和多窗口可以接受到此事件，便于更新视图的标题
     */
    private void notifyTitleUpdate() {
        EventMessage message = new EventMessage(2, "更新在view上网页的标题");
        EventBus.getDefault().post(message);
    }

    /**
     * @param arg        发生变化的Webview
     * @param i          webview在arraylist中的位置。
     * @param action     要执行的动作：添加，删除，或是更新
     * @param insertToDB 是否要加入数据库
     *                   <p>
     *                   如果webview传入的是null，那意味着这是一个新标签页，调用另一版本setData
     *                   观察者模式的更新操作！！！
     *                   网页载入了网址，触发观察者模式，这个方法，更新Convented_WebviewPage_List网页信息.
     *                   并且，根据“insertToDB”参数决定是否把“被更新的网页信息”加入历史记录数据库
     *                   并且，在更新网页信息的时候，还会更新数据库里这条记录的网页标题
     */
    private void notifyupdate(WebView arg, int i, Action action, boolean insertToDB) {
        //用传入的webview更新tmpData，后面需要用tmp进行封装
        WebPage_Info info = null;
        Log.d("网页管理", action.toString());

        if (action == Action.ADD) {
            //添加，添加的新标签页。下面这一条其实是遗留代码，但是没有必要删除，改动它会更费劲。
            info = setTmpData_newPage(SomeRes.homePage, SomeRes.default_homePage_url);

        } else if (action == Action.DELETE) {
            //删除
            info = setTmpData(null);
        } else {
            //更新
            info = setTmpData(arg);
            //更新数据库中网址的标题
            aboutHistory.updateTitle(info);
        }
        setChanged();
        //用封装的WebPageInfo执行推送
        notifyObservers(getSealedData(info, i, action));

        if (insertToDB) {
            //如果不是默认新标签页就加入数据库
            if (!(info.getUrl().equals(SomeRes.default_homePage_url))) {
                //历史记录加入数据库
                insertToDB(info);
            }
        }

    }

    /**
     * 插入数据库
     */
    private void insertToDB(WebPage_Info info) {
        aboutHistory.addToDataBase(info);
    }

    public boolean isempty() {
        return webViewArrayList.isEmpty();
    }


    /**
     * @param i 让位置i的网页停止加载
     */
    public void stop(int i) {
        webViewArrayList.get(i).onPause();
    }

    /**
     * @param i 让位置i的网页恢复加载
     */
    public void reStart(int i) {
        webViewArrayList.get(i).onResume();
    }


    @SuppressLint("SetJavaScriptEnabled")
    private void setWebview(WebView webView, AppCompatActivity context) {
        WebSettings settings = webView.getSettings();

        webView.canGoBack();
        webView.canGoForward();

        if (PreferenceTools.getBoolean(context, WebviewConf.customDownload)) {
            //内置下载器
            webView.setDownloadListener(new DownloadListener2(context));
        } else {
            //系统的下载器
            webView.setDownloadListener(new DownloadListener1(context));
        }

        // webview启用javascript支持 用于访问页面中的javascript
        settings.setJavaScriptEnabled(true);
        //设置WebView缓存模式 默认断网情况下不缓存
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        /*
         * LOAD_CACHE_ONLY: 不使用网络，只读取本地缓存数据
         * LOAD_DEFAULT: （默认）根据cache-control决定是否从网络上取数据。
         * LOAD_NO_CACHE: 不使用缓存，只从网络获取数据.
         * LOAD_CACHE_ELSE_NETWORK，只要本地有，无论是否过期，或者no-cache，都使用缓存中的数据。
         */
        //断网情况下加载本地缓存
        settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        //让WebView支持DOM storage API
        settings.setDomStorageEnabled(true);
        //字体缩放
        settings.setTextZoom(Integer.parseInt(PreferenceTools.getString(context, WebviewConf.textZoom, "100")));
        //让WebView支持缩放
        settings.setSupportZoom(true);
        //启用WebView内置缩放功能
        settings.setBuiltInZoomControls(true);
        //让WebView支持可任意比例缩放
        settings.setUseWideViewPort(true);
        //设置WebView使用内置缩放机制时，是否展现在屏幕缩放控件上
        settings.setDisplayZoomControls(false);
        //设置在WebView内部是否允许访问文件
        settings.setAllowFileAccess(true);
        settings.setNeedInitialFocus(true);
        settings.setBlockNetworkImage(false);
        //设置WebView的访问UserAgent
        settings.setUserAgentString(PreferenceTools.getString(context, WebviewConf.userAgent));
        //设置脚本是否允许自动打开弹窗
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        // 开启Application H5 Caches 功能
        settings.setAppCacheEnabled(true);
        // 设置编码格式
        settings.setDefaultTextEncodingName("utf-8");
        // 开启数据库缓存
        settings.setDatabaseEnabled(true);
        //打开新的窗口，如果是true，在webchromeclient中处理，这里我已经用长按菜单实现了，没必要再用这个方法
        settings.setSupportMultipleWindows(false);

    }

    /**
     * @param pos     当前webview在list中的位置
     * @param content 文本框输入的内容
     */
    public void findAllAsync(int pos, String content) {
        webViewArrayList.get(pos).setFindListener(new WebView.FindListener() {
            @Override
            public void onFindResultReceived(int i, int i1, boolean b) {
                /*
                 *  void onFindResultReceived (int activeMatchOrdinal,int numberOfMatches,boolean isDoneCounting)
                 *
                 * activeMatchOrdinal：指示这是第几个被匹配的文本，下标从0开始
                 * numberOfMatches：一共匹配了多少个
                 * isDoneCounting：匹配文本是否完成
                 * */

            }
        });

        webViewArrayList.get(pos).findAllAsync(content);
    }


    public void clearMatches(int pos) {
        webViewArrayList.get(pos).clearMatches();
    }

    /**
     * @param pos 当前webview在list中的位置
     *            查找下一个匹配的文字
     */
    public void goaHead(int pos) {
        //true为查找下一个
        webViewArrayList.get(pos).findNext(true);
    }

    /**
     * @param pos 当前webview在list中的位置
     *            查找上一个匹配的文字
     */
    public void text_back(int pos) {
        //回到上一个
        webViewArrayList.get(pos).findNext(false);
    }


}
