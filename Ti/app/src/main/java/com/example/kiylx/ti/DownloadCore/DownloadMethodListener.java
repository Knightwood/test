package com.example.kiylx.ti.DownloadCore;

/**
 * 提供给service调用下载器方法的接口。
 */
public interface DownloadMethodListener {
    void onProgress(int progress);

    void onSuccess();

    void onFailed();

    void onPaused();

    void onCanceled();
}