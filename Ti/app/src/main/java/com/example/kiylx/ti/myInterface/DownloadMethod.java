package com.example.kiylx.ti.myInterface;

import com.example.kiylx.ti.corebase.DownloadInfo;


/**
 * 定义的下载接口
 */
public interface DownloadMethod {
    /**
     * @param info 文件信息
     * @param fileName 修改的文件名称
     * @param filePath 修改的文件存储路径
     * @return 被修改后的文件信息
     */
    DownloadInfo setInfo(DownloadInfo info, String fileName, String filePath);
    void startDownload(DownloadInfo info);
    void startAll();
    void resumeDownload(DownloadInfo info);
    void resumeAll();
    void pauseDownload(DownloadInfo info);
    void pauseAll();
    void canaelDownload(DownloadInfo info);
    void cancelAll();
    float getRate(DownloadInfo info);
}
