package com.example.kiylx.ti.downloadpack.dinterface;

import com.example.kiylx.ti.downloadpack.core.DownloadInfo;

import java.util.List;

/**
 * 创建者 kiylx
 * 创建时间 2020/9/5 21:17
 * packageName：com.example.kiylx.ti.downloadpack.downloadcore
 * 描述：simpleDownloadManager会在初始化完成后（从数据库读取数据之类的），调用这些方法将下载信息传出
 */
public interface SendData {

    /**
     * @param downloading 包含除下载完成之外的下载信息列表
     */
    void allDownloading(List<DownloadInfo> downloading);

    void readyDownload(List<DownloadInfo> ready);

    void downloading(List<DownloadInfo> downloading);

    void pausedownload(List<DownloadInfo> paused);

    void completeDownload(List<DownloadInfo> complete);

    void notifyUpdate();

}
