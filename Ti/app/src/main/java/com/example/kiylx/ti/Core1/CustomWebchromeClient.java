package com.example.kiylx.ti.Core1;

import android.graphics.Bitmap;
import android.webkit.ConsoleMessage;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.example.kiylx.ti.INTERFACE.NotifyWebViewUpdate;

public class CustomWebchromeClient extends WebChromeClient {
    private static NotifyWebViewUpdate mNotifyWebViewUpdate;

    public static void setInterface(NotifyWebViewUpdate minterface){
        CustomWebchromeClient.mNotifyWebViewUpdate=minterface;

    }



    @Override
    public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
        return super.onConsoleMessage(consoleMessage);
    }

    /**
     * 当前 WebView 加载网页进度
     *
     * @param view WebView
     * @param newProgress 进度
     */
    @Override
    public void onProgressChanged(WebView view, int newProgress) {
        super.onProgressChanged(view, newProgress);
            if(newProgress==100){
                mNotifyWebViewUpdate.notifyWebViewUpdate(view);
            }
    }

    /**
     * Js 中调用 alert() 函数，产生的对话框
     *
     * @param view Webview
     * @param url url
     * @param message
     * @param result
     * @return
     */
    @Override
    public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
        return super.onJsAlert(view, url, message, result);
    }

    /**
     * 处理 Js 中的 Confirm 对话框
     *
     * @param view
     * @param url
     * @param message
     * @param result
     * @return
     */
    @Override
    public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
        return super.onJsConfirm(view, url, message, result);
    }

    /**
     * 处理 JS 中的 Prompt对话框
     *
     * @param view
     * @param url
     * @param message
     * @param defaultValue
     * @param result
     * @return
     */
    @Override
    public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
        return super.onJsPrompt(view, url, message, defaultValue, result);
    }

    /**
     * 接收web页面的icon
     *
     * @param view
     * @param icon
     */
    @Override
    public void onReceivedIcon(WebView view, Bitmap icon) {
        super.onReceivedIcon(view, icon);
    }

    /**
     * 接收web页面的 Title
     *
     * @param view
     * @param title
     */
    @Override
    public void onReceivedTitle(WebView view, String title) {
        super.onReceivedTitle(view, title);
    }
}
