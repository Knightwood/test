package com.example.kiylx.ti.Corebase;

import com.example.kiylx.ti.DownloadCore.DownloadTask;

public class DownloadInfo {
    private DownloadTask task;
    private String url;
    private String fileName;
    private String pith;
    private double totalProcress;
    private long fileLength;

    public DownloadTask getTask() {
        return task;
    }

    public void setTask(DownloadTask task) {
        this.task = task;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getPith() {
        return pith;
    }

    public void setPith(String pith) {
        this.pith = pith;
    }

    public double getTotalProcress() {
        return totalProcress;
    }

    public void setTotalProcress(double totalProcress) {
        this.totalProcress = totalProcress;
    }

    public long getFileLength() {
        return fileLength;
    }

    public void setFileLength(long fileLength) {
        this.fileLength = fileLength;
    }


}
