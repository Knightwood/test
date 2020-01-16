package com.example.kiylx.ti.model;

import android.view.View;

public class DownloadControlViewModel {
    private long totalDownload;
    private long length;
    private String fileUrl;
    /**
     * true时调用download方法
     * false时调用pause方法
     */
    public boolean cheak;

    public long getTotalDownload() {
        return totalDownload;
    }

    public void setTotalDownload(long totalDownload) {
        this.totalDownload = totalDownload;
    }

    public long getLength() {
        return length;
    }

    public void setLength(long length) {
        this.length = length;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public interface ClickMethod {
        void tr(View view);

        void download();

        void pause();

        void cancel();
    }


}
