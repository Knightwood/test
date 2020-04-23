package com.example.kiylx.ti.myInterface;

import android.content.Intent;
import android.net.Uri;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;

/**
 * 创建者 kiylx
 * 创建时间 2020/4/22 16:29
 */
public interface FileUpload {
    boolean upload(ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams);
}
