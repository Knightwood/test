package com.example.kiylx.ti.interfaces;

import android.webkit.WebView;

public interface NotifyWebViewUpdate {
    /**
     * @param webView webview
     *                网页加载完成时调用它更新某些信息,并加入数据库
     */
    void updateWebViewInfo(WebView webView);

    /**
     * 只更新webpageinfo，和标题，但是不加入数据库
     * @param webView
     */
    void updateTitle(WebView webView);
}
