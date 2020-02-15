package com.example.kiylx.ti.myInterface;

import android.webkit.WebView;

public interface NotifyWebViewUpdate {
    /**
     * @param webView webview
     *                网页加载完成时调用它更新某些信息
     */
    void updateWebViewInfo(WebView webView);
}
