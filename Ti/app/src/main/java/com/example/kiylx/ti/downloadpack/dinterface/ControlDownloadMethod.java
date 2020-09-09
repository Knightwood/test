package com.example.kiylx.ti.downloadpack.dinterface;

import com.example.kiylx.ti.downloadpack.core.DownloadInfo;

import java.util.List;

/**
 * dwonloadService实现此接口，便于downloadActivity获取下载列表、信息、控制下载过程
 */
public interface ControlDownloadMethod {

    void download(DownloadInfo info) throws Exception;

    void pause(DownloadInfo info);

    void cancel(DownloadInfo info);

    void reasume(DownloadInfo info);

    float getPercent(DownloadInfo info);

    List<DownloadInfo> getAllDownload();

    List<DownloadInfo> getAllComplete();

    void getAllDownload(List<DownloadInfo> list);

    void getAllComplete(List<DownloadInfo> list);

    boolean isNotWork();
}
