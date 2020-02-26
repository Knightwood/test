package com.example.kiylx.ti.core1;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProviders;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceManager;

import com.example.kiylx.ti.downloadCore.DownloadListener1;
import com.example.kiylx.ti.downloadCore.DownloadListener2;

/**
 * webview的设置
 */
public class WebiVewSetting {
    private static final String TAG="webview设置";
    private SharedPreferences sharedPreferences;


    @SuppressLint("SetJavaScriptEnabled")
    public static void set1(WebView webView, AppCompatActivity context) {

        webView.canGoBack();
        webView.canGoForward();

        //系统的下载器
        webView.setDownloadListener(new DownloadListener1(context));
        //内置下载器
        //webView.setDownloadListener(new DownloadListener2(context));

        WebSettings settings = webView.getSettings();
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
        //设置WebView的访问UserAgent
        settings.setUserAgentString(null);
        //设置脚本是否允许自动打开弹窗
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        // 开启Application H5 Caches 功能
        settings.setAppCacheEnabled(true);
        // 设置编码格式
        settings.setDefaultTextEncodingName("utf-8");
        // 开启数据库缓存
        settings.setDatabaseEnabled(true);
        //打开新的窗口
        settings.setSupportMultipleWindows(false);

    }

    private String get_user_agent(Context context){
        if (sharedPreferences==null)
        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(context);
        Log.d(TAG, "获取浏览器标识: "+ sharedPreferences.getString("explorer_flags",null));
        String user_agent=sharedPreferences.getString("explorer_flags",null);
        if (user_agent.equals("0"))
            return null;
        return user_agent;

    }
}
