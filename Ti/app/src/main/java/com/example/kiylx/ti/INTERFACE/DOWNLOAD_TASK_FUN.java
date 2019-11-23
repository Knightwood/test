package com.example.kiylx.ti.INTERFACE;

import com.example.kiylx.ti.Corebase.DownloadInfo;

public interface DOWNLOAD_TASK_FUN {

    /**
     * @return 暂停下载的一些操作
     */
     boolean pausedDownload(DownloadInfo info);

    /**
     * @return 取消下载的一些操作
     */
    boolean canceledDownload(DownloadInfo info);

    boolean downloadSucess(DownloadInfo info);

}
