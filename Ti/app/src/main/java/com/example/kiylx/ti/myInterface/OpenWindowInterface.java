package com.example.kiylx.ti.myInterface;

import android.content.Context;
import android.webkit.WebView;

/**
 * 创建者 kiylx
 * 创建时间 2020/4/24 16:01
 * 用于处理WebChromeClient在打开新窗口时调用，把信息传递给MainActivity，使其调用webviewManager的方法打开新窗口
 */
public interface OpenWindowInterface {
    WebView OpenWindow(Context url);
}
