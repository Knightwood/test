package com.example.kiylx.ti.downloadpack.core;

/**
 * 描述下载任务的行为变化，开始下载，暂停，取消，删除文件等等，均会在downloadmanager中发出通知
 */
public enum notifyAction {
    complete,startDwonload,cancel,delete,paused,resume
}
