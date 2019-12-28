package com.example.kiylx.ti.DownloadCore;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.webkit.DownloadListener;

/**
 * 调用系统自带的下载器下载
 */
public class MydownloadListener implements DownloadListener {
    private Context mContext;

    public MydownloadListener(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        this.mContext.startActivity(intent);
    }
}
