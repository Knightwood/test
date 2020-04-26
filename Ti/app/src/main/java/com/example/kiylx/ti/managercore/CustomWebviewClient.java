package com.example.kiylx.ti.managercore;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.util.Log;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.example.kiylx.ti.interfaces.NotifyWebViewUpdate;

import java.net.URISyntaxException;
import java.util.List;

public class CustomWebviewClient extends WebViewClient {
    private Context mContext;
    private static NotifyWebViewUpdate mNotifyWebViewUpdate;


    public CustomWebviewClient(Context context){
        //用构造函数把context传进来，用来初始化getTitle接口，此接口用来传回网页标题
        mContext=context;
    }
    public static void setInterface(NotifyWebViewUpdate minterface) {
        CustomWebviewClient.mNotifyWebViewUpdate = minterface;

    }
    /**
     * 是否在 WebView 内加载页面
     * 当要在当前WebView中加载新的URL时，给主机应用程序一个接管控件的机会。
     * 如果未提供WebViewClient，则默认情况下，WebView将要求活动管理器为URL选择适当的处理程序。
     * 如果提供了WebViewClient，则返回true表示主机应用程序处理该URL，而返回false表示当前的WebView处理该URL。
     */
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url){
        //WebResourceRequest request
        /*try {
            if (url.startsWith("http:") || url.startsWith("https:")) {
                view.loadUrl(url);
            } else {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                //BookmarktActivity(intent);
            }
            return true;
        } catch (Exception e){
            return false;
        }*/
        //Android8.0以下的需要返回true 并且需要loadUrl；8.0之后效果相反
        /*if(Build.VERSION.SDK_INT < 26) {
            view.loadUrl(url);
            return true;
        } else {
            return false;
        }*/
        try {
            //处理intent协议
            if (url.startsWith("intent://")) {
                Intent intent;
                try {
                    intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
                    intent.addCategory("android.intent.category.BROWSABLE");
                    intent.setComponent(null);
                    /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                        intent.setSelector(null);
                    }*/
                    List<ResolveInfo> resolves = mContext.getPackageManager().queryIntentActivities(intent,0);
                    if(resolves.size()>0){
                        mContext.startActivity(intent);
                    }
                    return true;
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            }
            // 处理自定义scheme协议
            if (!url.startsWith("http")) {
                Log.e("处理scheme","处理自定义scheme:" + url);
                try {
                    //以下固定写法
                    final Intent intent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse(url));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                            | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    mContext.startActivity(intent);
                } catch (Exception e) {
                    // 防止没有安装的情况
                    e.printStackTrace();
                    Toast.makeText(mContext,"您所打开的第三方App未安装！", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;

    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        return super.shouldOverrideUrlLoading(view, request);
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
        // 更新工具栏上的文字
        super.onPageFinished(view, url);
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

    /**
     * @param view
     * @param url
     * @param isReload
     * 这个方法会通知给host application更新网址
     */
    @Override
    public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {
        super.doUpdateVisitedHistory(view, url, isReload);
        //通知更新网址
        mNotifyWebViewUpdate.updateWebViewInfo(view);
    }
}
