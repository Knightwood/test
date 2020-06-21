package com.example.kiylx.ti.webview32;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Message;
import android.util.Log;
import android.webkit.ConsoleMessage;
import android.webkit.GeolocationPermissions;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.example.kiylx.ti.interfaces.WebViewChromeClientInterface;
import com.example.kiylx.ti.interfaces.NotifyWebViewUpdate;


public class CustomWebchromeClient extends WebChromeClient {
    private static NotifyWebViewUpdate mNotifyWebViewUpdate;
    private static final String TAG = "CustomWebchromeClient";
    private WebViewChromeClientInterface clientInterface;


    public static void setInterface(NotifyWebViewUpdate minterface) {
        CustomWebchromeClient.mNotifyWebViewUpdate = minterface;
    }


    @Override
    public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
        return super.onConsoleMessage(consoleMessage);
    }

    /**
     * 当前 WebView 加载网页进度
     *
     * @param view        WebView
     * @param newProgress 进度
     */
    @Override
    public void onProgressChanged(WebView view, int newProgress) {
        super.onProgressChanged(view, newProgress);
        mNotifyWebViewUpdate.updateProgress(view,newProgress);

        if (newProgress == 100) {
            //在webviewClient中更新了网址，在网页加载完成时再更新一次
            mNotifyWebViewUpdate.updateTitle(view);
            Log.d(TAG, "onProgressChanged: 网页加载完成,更新标题");
        }


    }

    /**
     * Js 中调用 alert() 函数，产生的对话框
     *
     * @param view    Webview
     * @param url     url
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

    /**
     * @param webView
     * @param filePathCallback  执行它的onReceiveValue方法把获取到的uri信息传给webview。
     * @param fileChooserParams 可以使用它根据请求，创建合适的intent，然后利用这个intent启动“文件选择器”，
     *                          选择文件后，会在onactivityresulr中拿到uri结果，把结果给filePathCallback处理以上传文件。
     * @return
     */
    @Override
    public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
        return clientInterface.upload(filePathCallback, fileChooserParams);

    }

    /**
     * @param view          请求新窗口的WebView
     * @param isDialog      如果是true，代表这个新窗口只是个对话框，如果是false，则是一个整体的大小的窗口
     * @param isUserGesture 如果是true，代表这个请求是用户触发的，例如点击一个页面上的一个连接
     * @param resultMsg     当一个新的WebView被创建时这个只被传递给他，
     *                      resultMsg.obj是一个WebViewTransport的对象，它被用来传送给新创建的WebView
     *                      使用方法：WebView.WebViewTransport.setWebView(WebView)
     * @return 这个方法如果返回true，代表这个主机应用会创建一个新的窗口，否则应该返回fasle。如果你返回了false，但是依然发送resulMsg会导致一个未知的结果。
     */
    @Override
    public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {

        if (isUserGesture) {
            if (clientInterface == null) {
                return false;
            } else {
                WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
                WebView tmp = clientInterface.OpenWindow(view.getContext());//这个context就包含了要打开的网页，这样new出来的webview就是要打开的新窗口的网页
                transport.setWebView(tmp);
                resultMsg.sendToTarget();
                return true;

            }
        }
        return super.onCreateWindow(view, isDialog, isUserGesture, resultMsg);
    }

    //mainactivity中实现，mainactivity把实现传入webviewmanager，再传到这里。
    public void setClientInterface(WebViewChromeClientInterface iupload) {
        this.clientInterface = iupload;
    }

    /**
     * @param origin
     * @param callback
     * 处理请求地理位置
     */
    @Override
    public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
        if (clientInterface!=null){
           clientInterface.requestLocate(origin,callback);
        }
    }
}
