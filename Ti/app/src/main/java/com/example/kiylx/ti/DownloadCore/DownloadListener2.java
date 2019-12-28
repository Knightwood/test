package com.example.kiylx.ti.DownloadCore;

import android.webkit.DownloadListener;

import com.example.kiylx.ti.Corebase.DownloadInfo;

/**
 * 启动自己写的多线程下载其进行下载
 */
public class DownloadListener2 implements DownloadListener {
    @Override
    public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
        DownloadInfo info=new DownloadInfo(url,contentLength);
    }
}
