package com.example.kiylx.ti.interfaces;

import android.content.Context;
import android.net.Uri;
import android.webkit.GeolocationPermissions;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

/**
 * 创建者 kiylx
 * 创建时间 2020/4/22 16:29
 */
public interface WebViewChromeClientInterface {
    /**
     * @param filePathCallback
     * @param fileChooserParams
     * @return
     */
    boolean upload(ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams);

    /**
     * @param url
     * @return
     * 用于处理WebChromeClient在打开新窗口时调用，把信息传递给MainActivity，使其调用webviewManager的方法打开新窗口
     */
    WebView OpenWindow(Context url);

    /**
     * 用于处理请求地理位置弹窗
     * @param origin
     * @param callback
     */
    void requestLocate(String origin, GeolocationPermissions.Callback callback);
}
