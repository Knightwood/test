package com.example.kiylx.ti.INTERFACE;

public interface DOWNLOAD_TASK_FUN {
    /**
     * @return 主线程中根据分块信息里计算下载进度
     */
    long updateProcess();

    /**
     * @return 暂停下载的一些操作
     */
    boolean pausedDownload();

    /**
     * @return 取消下载的一些操作
     */
    boolean canceledDownload();

    boolean downloadSucess();
}
