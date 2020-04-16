package com.example.kiylx.ti.downloadPack.dFragments;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

/**
 * 创建者 kiylx
 * 创建时间 2020/3/23 17:34
 * downloadinfo转换成downloadEntity存入数据库，再转换成simpleDownloadInfo用于界面显示
 */
public class SimpleDownloadInfo extends BaseObservable {
    private String id;//用于标识，是唯一的
    private String fileName;//文件名称
    private String url;//网址
    private boolean pause;//暂停标记
    private boolean wait;//等待标记
    private boolean cancel;//取消标记
    private float precent;//已完成下载的百分比


    public SimpleDownloadInfo(String fileName, String url, boolean pause, boolean wait, boolean cancel, float precent) {
        this.fileName = fileName;
        this.url = url;
        this.pause = pause;
        this.wait = wait;
        this.cancel = cancel;
        this.precent = precent;
    }

    public SimpleDownloadInfo(String fileName, String url, boolean pause, boolean wait, boolean cancel, long currentLength, long contentLength) {
        this(fileName, url, pause, wait, cancel, (float) currentLength / contentLength);
    }

    @Bindable
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Bindable
    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Bindable
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Bindable
    public boolean isPause() {
        return pause;
    }

    public void setPause(boolean pause) {
        this.pause = pause;
    }

    @Bindable
    public boolean isWait() {
        return wait;
    }

    public void setWait(boolean wait) {
        this.wait = wait;
    }

    @Bindable
    public boolean isCancel() {
        return cancel;
    }

    public void setCancel(boolean cancel) {
        this.cancel = cancel;
    }

    @Bindable
    public float getPrecent() {
        return this.precent*100;
    }

    public void setPrecent(float precent) {
        this.precent = precent;
        notify();
    }
}
