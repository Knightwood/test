package com.example.kiylx.ti.downloadpack.dinterface;

import com.example.kiylx.ti.downloadpack.bean.DownloadInfo;

/**
 * downloadmanager实现了这些接口，在下载的线程中可以调用这些方法来控制。
 */
public interface DOWNLOAD_TASK_FUN {

    /**
     * @return 暂停下载的一些操作
     */
     boolean downloadPaused(DownloadInfo info);

    /**
     * @return 取消下载的一些操作
     */
    //boolean canceledDownload(DownloadInfo info);

    boolean downloadSucess(DownloadInfo info);
}
