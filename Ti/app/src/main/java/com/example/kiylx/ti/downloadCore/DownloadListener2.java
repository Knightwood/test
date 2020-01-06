package com.example.kiylx.ti.downloadCore;

import android.webkit.DownloadListener;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.example.kiylx.ti.corebase.DownloadInfo;
import com.example.kiylx.ti.myFragments.DownloadWindow;

/**
 * 启动自己写的多线程下载其进行下载
 */
public class DownloadListener2 implements DownloadListener {
    private AppCompatActivity mContext;
    public DownloadListener2(AppCompatActivity context) {
        this.mContext=context;
    }

    /**
     * @param url 下载地址
     * @param userAgent 标识
     * @param contentDisposition 文件描述
     * @param mimetype 不知道做什么的
     * @param contentLength 文件长度
     *                      拿到信息，打开一个下载dialog界面。
     */
    @Override
    public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
        DownloadInfo info=new DownloadInfo(url,contentLength);
        DownloadWindow dialog= DownloadWindow.getInstance(info);
        FragmentManager manager=mContext.getSupportFragmentManager();
        dialog.show(manager,"下载");

    }
}
