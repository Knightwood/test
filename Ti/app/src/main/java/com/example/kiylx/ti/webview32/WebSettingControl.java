package com.example.kiylx.ti.webview32;

import android.annotation.SuppressLint;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.kiylx.ti.conf.SomeRes;
import com.example.kiylx.ti.downloadpack.downloadcore.DownloadListener1;
import com.example.kiylx.ti.downloadpack.downloadcore.DownloadListener2;
import com.example.kiylx.ti.tool.preferences.DefaultPreferenceTool;
import com.example.kiylx.ti.tool.preferences.PreferenceTools;
import com.example.kiylx.ti.tool.SomeTools;
import com.example.kiylx.ti.ui.activitys.MainActivity;

import java.util.Observable;
import java.util.Observer;

/**
 * 创建者 kiylx
 * 创建时间 2020/7/5 22:36
 */
public class WebSettingControl implements Observer {
    private static final String TAG = "webSteeing";
    private final Observable observable;
    private CookieManager cookieManager;
    private static WebSettingControl webSettingControl;

    public static WebSettingControl getInstance(Observable o, CookieManager cookieManager) {
        if (webSettingControl == null) {
            webSettingControl = new WebSettingControl(cookieManager, o);
        }
        return webSettingControl;
    }

    public WebSettingControl(CookieManager cookieManager, Observable o) {
        this.cookieManager = cookieManager;
        this.observable = o;
        observable.addObserver(this);
    }

    @SuppressLint("SetJavaScriptEnabled")
    public static void ConfigWebview(WebView webView, AppCompatActivity context, CookieManager cookieManager) {
        WebSettings settings = webView.getSettings();

        webView.canGoBack();
        webView.canGoForward();

        if (DefaultPreferenceTool.getBoolean(context, "custom_download_listener", false) && MainActivity.IsVertical()) {
//横屏时不使用自己写的下载器
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
        settings.setTextZoom(DefaultPreferenceTool.getInt(context, "text_zoom", 100));
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
        //settings.setNeedInitialFocus(true);
        //是否禁止图片显示
        // LogUtil.d(TAG, "ConfigWebview:应该图片显示？ "+SomeTools.getXapplication().getStateManager().canShowPic());
        settings.setBlockNetworkImage(!SomeTools.getXapplication().getStateManager().canShowPic());
        //设置WebView的访问UserAgent
        if (SomeTools.getXapplication().getStateManager().getPcMode()) {
            settings.setUserAgentString(SomeRes.PCuserAgent);
        } else
            settings.setUserAgentString(PreferenceTools.getString(context, "user_agent", null));
        //设置脚本是否允许自动打开弹窗
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setAllowContentAccess(false);//内容Url访问允许WebView从安装在系统中的内容提供者载入内容。
        // 开启Application H5 Caches 功能
        settings.setAppCacheEnabled(true);
        // 设置编码格式
        settings.setDefaultTextEncodingName("utf-8");
        // 开启数据库缓存
        settings.setDatabaseEnabled(true);
        //打开新的窗口，如果是true，在webchromeclient中处理，这里我已经用长按菜单实现了，没必要再用这个方法
        settings.setSupportMultipleWindows(true);
        //允许http和https混用
        settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        //启用地理位置
        settings.setGeolocationEnabled(true);
        //设置定位的数据库路径
        //String dir = Xapplication.getInstance().getDir("database", Context.MODE_PRIVATE).getPath();
        //settings.setGeolocationDatabasePath(dir);
        //cookies设置
        cookieManager.setAcceptThirdPartyCookies(webView, PreferenceTools.getBoolean(context, "use_third_cookies", false));
    }

    @Override
    public void update(Observable o, Object arg) {

    }
}
