package com.example.kiylx.ti.model;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class CustomWebviewClient extends WebViewClient {
    private sendTitle mSendTitle;
    private Context mContext;

    public interface sendTitle{
       void setInfos(String title, String url);
    }
    public CustomWebviewClient(Context context){
        //用构造函数把context传进来，用来初始化getTitle接口，此接口用来传回网页标题
        mContext=context;
    }
    /**
     * 是否在 WebView 内加载页面
     *
     */
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url){
        //WebResourceRequest request
        //return false;
        try {
            if (url.startsWith("http:") || url.startsWith("https:")) {
                view.loadUrl(url);
            } else {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                //startActivity(intent);
            }
            return true;
        } catch (Exception e){
            return false;
        }
    }

    /**
     * 当WebView得页面Scale值发生改变时回调
     */
    @Override
    public void onScaleChanged(WebView view,float oldScale,float newScale){
        super.onScaleChanged(view,oldScale,newScale);
    }
    /**
     * WebView 开始加载页面时回调，一次Frame加载对应一次回调
     *
     * @param view
     * @param url
     * @param favicon
     */
    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);

    }

    /**
     * WebView 完成加载页面时回调，一次Frame加载对应一次回调
     *
     * @param view
     * @param url
     */
    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        mSendTitle=(sendTitle) mContext;
        mSendTitle.setInfos(view.getTitle(),url);
    }
    /**
     * WebView 加载页面资源时会回调，每一个资源产生的一次网络加载，除非本地有当前 url 对应有缓存，否则就会加载。
     *
     * @param view WebView
     * @param url  url
     */
    @Override
    public void onLoadResource(WebView view, String url) {
        super.onLoadResource(view, url);
    }
    /**
     * WebView 可以拦截某一次的 request 来返回我们自己加载的数据，这个方法在后面缓存会有很大作用。
     *
     * @param view    WebView
     * @param request 当前产生 request 请求
     * @return WebResourceResponse
     */
    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
        return super.shouldInterceptRequest(view, request);
    }
    /**
     * WebView 访问 url 出错
     *
     * @param view
     * @param request
     * @param error
     */
    @Override
    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
        super.onReceivedError(view, request, error);
    }
    /**
     * WebView ssl 访问证书出错，handler.cancel()取消加载，handler.proceed()对然错误也继续加载
     *
     * @param view
     * @param handler
     * @param error
     */
    @Override
    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
        super.onReceivedSslError(view, handler, error);
    }
}
