package com.example.kiylx.ti.mvp.presenter;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.webkit.GeolocationPermissions;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.kiylx.ti.tool.LogUtil;
import com.example.kiylx.ti.xapplication.Xapplication;
import com.example.kiylx.ti.interfaces.WebViewChromeClientInterface;
import com.example.kiylx.ti.tool.preferences.DefaultPreferenceTool;

import java.lang.ref.WeakReference;

import pub.devrel.easypermissions.EasyPermissions;

/**
 * 创建者 kiylx
 * 创建时间 2020/7/11 21:43
 */
public class ChromeClientInterfaceImpl implements WebViewChromeClientInterface {
    String[] locatePerm = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
    private static final String TAG = "ChromeClientInte";
    private WeakReference<AppCompatActivity> context = null;
    //false则是自己写的方法，能上传所有文件，不去理会是什么type，默认值是true，以系统提供的createIntent方式获取文件。
    boolean defUploadMode = DefaultPreferenceTool.getBoolean(Xapplication.getInstance(), "def_upload_mode", true);

    public ChromeClientInterfaceImpl(AppCompatActivity appCompatActivity) {
        if (this.context == null)
            this.context = new WeakReference<>(appCompatActivity);
    }

    @Override
    public boolean upload(ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {
        //cancelUpLoad();//清理一次请求，防止上一次的没有消费掉而出错
        //fileUploadCallBack = filePathCallback;
        try {
            if (!defUploadMode) {
                //选择文件
                Intent intent2 = new Intent(Intent.ACTION_GET_CONTENT);
                intent2.addCategory(Intent.CATEGORY_OPENABLE);
                intent2.setType("*/*");
                context.get().startActivityForResult(Intent.createChooser(intent2, "上传文件"), 2020);
            } else {
                Intent intent = fileChooserParams.createIntent();
                LogUtil.d(TAG, "onShowFileChooser: type:" + intent.getType() + " action:" + intent.getAction() + " category:" + intent.getCategories());
                context.get().startActivityForResult(Intent.createChooser(intent, "上传文件"), 2020);
            }

        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            return true;
        }
        return true;


    }

    /**
     * webview对于
     * <a href="http://www.google.com" target="_blank">new window</a>
     * 这种形式的tag，会要求起一个新窗口打开，而要截获这个请求，则可以在webChromeClient的onCreateWindow()回调函数中进行处理，
     */
    @Override
    public WebView OpenWindow(Context url) {
       /* mWebViewManager.stop(current);
        f1.removeView(mWebViewManager.getTop(current));
        CustomAWebView tmp = mWebViewManager.newWebview(++current, url, context.get());
        f1.addView(mWebViewManager.getTop(current));
        return tmp;*/
       return null;
    }

    /**
     * @param origin   请求地理位置的网址
     * @param callback 处理请求的回调接口，调用invoke方法处理是否给于origin地理位置
     */
    @Override
    public void requestLocate(String origin, GeolocationPermissions.Callback callback) {
        //如果有存储权限，则可以开始下载，否则告诉用户申请权限
        if (!EasyPermissions.hasPermissions(context.get(), locatePerm)) {
            EasyPermissions.requestPermissions(context.get(), "没有地理位置权限，请去设置给予权限后再试", 20033, locatePerm);
            callback.invoke(origin, false, false);
            return;
        }
        //GeolocationPermissions.getInstance().allow(origin);
        new AlertDialog.Builder(context.get()).setMessage("当前网址想要使用你的地理位置")
                .setPositiveButton("允许", (dialog, which) -> {
                    callback.invoke(origin, true, false);
                    LogUtil.d(TAG, "requestLocate: 位置请求成功");
                })
                .setNegativeButton("拒绝", (dialog, which) -> {
                    callback.invoke(origin, false, false);
                    LogUtil.d(TAG, "requestLocate: 位置请求失败");
                })
                .setCancelable(false)
                .show();
    }

}
