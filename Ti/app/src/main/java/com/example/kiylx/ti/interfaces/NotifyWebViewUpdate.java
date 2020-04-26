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

    /**
     * @param progress 网页加载进度
     *                 webchromeclient中调用此方法，通知主界面更新网页加载进度条的进度
     */
    void updateProgress(int progress);
}
