package com.example.kiylx.ti.downloadpack.downloadcore;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.webkit.DownloadListener;

import com.example.kiylx.ti.tool.LogUtil;

/**
 * 调用系统自带的下载器下载
 */
public class DownloadListener1 implements DownloadListener {
    private Context mContext;

    private static final String TAG ="下载监听";

    public DownloadListener1(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        this.mContext.startActivity(intent);

        LogUtil.d(TAG, "onDownloadStart:下载地址 "+url);
        LogUtil.d(TAG, "onDownloadStart: 标识"+userAgent);
        LogUtil.d(TAG, "onDownloadStart: 文件大小"+contentLength);
    }
}
