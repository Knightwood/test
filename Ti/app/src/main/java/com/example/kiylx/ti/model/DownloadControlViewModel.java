package com.example.kiylx.ti.model;

import com.example.kiylx.ti.corebase.DownloadInfo;
import com.example.kiylx.ti.myInterface.DownloadClickMethod;

public class DownloadControlViewModel {
    private DownloadInfo mDownloadInfo;

    /**
     * 下载的进度
     */
    private int procress;
    private String fileUrl;
    private String filename;
    /**
     * true时调用download方法
     * false时调用pause方法
     */
    public boolean cheak;

    private DownloadClickMethod mInterface;

    public DownloadControlViewModel(DownloadClickMethod method) {
        this.mInterface = method;
    }

    /**
     * @param downloadInfo 下载信息。
     *
     *                     设置好下载信息，方便xml访问这些数据。
     */
    public void setDownloadInfo(DownloadInfo downloadInfo) {
        mDownloadInfo = downloadInfo;

        this.setFileUrl(downloadInfo.getUrl());
        this.setFilename(downloadInfo.getFileName());
        this.procress =((int)(downloadInfo.getProcress()*100));
    }
    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    /**
     * xml控制下载或时暂停。
     *
     */
    public void downloadandpause() {
        if (cheak) {
            mInterface.download(mDownloadInfo);
        } else {
            mInterface.pause(mDownloadInfo);
        }
    }

    public void cancelDownload() {
        mInterface.cancel(mDownloadInfo);
    }




    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public int getProcress() {
        return (int)(mDownloadInfo.getProcress()*100);
    }
}
