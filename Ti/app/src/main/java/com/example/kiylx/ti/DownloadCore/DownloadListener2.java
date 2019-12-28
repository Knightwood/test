package com.example.kiylx.ti.DownloadCore;

import android.content.Context;
import android.webkit.DownloadListener;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.example.kiylx.ti.Corebase.DownloadInfo;
import com.example.kiylx.ti.Fragments.DownloadWindow;

/**
 * 启动自己写的多线程下载其进行下载
 */
public class DownloadListener2 implements DownloadListener {
    private AppCompatActivity mContext;
    public DownloadListener2(AppCompatActivity context) {
        this.mContext=context;
    }

    @Override
    public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
        DownloadInfo info=new DownloadInfo(url,contentLength);
        DownloadWindow dialog= DownloadWindow.getInstance(info);
        FragmentManager manager=mContext.getSupportFragmentManager();
        dialog.show(manager,"下载");

    }
}
