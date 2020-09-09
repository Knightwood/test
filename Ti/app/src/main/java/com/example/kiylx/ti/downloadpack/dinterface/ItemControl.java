package com.example.kiylx.ti.downloadpack.dinterface;

import com.example.kiylx.ti.downloadpack.core.DownloadInfo;

/**
 * 创建者 kiylx
 * 创建时间 2020/9/5 17:12
 * packageName：com.example.kiylx.ti.downloadpack.dinterface
 * 描述：提供给下载列表中item的控制下载的方法。
 */
public interface ItemControl {

    void download(DownloadInfo info) throws Exception;

    void pause(DownloadInfo info);

    void cancel(DownloadInfo info);

    void resume(DownloadInfo info);

    float getPercent(DownloadInfo info);
}
