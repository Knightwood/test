package com.example.kiylx.ti.downloadPack.downloadInfo_storage;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "downloadInfo_tab", indices = {@Index(value = {"url"})})

public class DownloadEntity {

    /**
     * @param url              下载网址
     * @param filename         文件名称
     * @param path             文件存放路径
     * @param blockCompleteNum 分块下载时已完成下载的分块数量
     * @param blockPauseNum    分块下载时调用“暂停：时分块暂停的数量
     * @param pause            暂停标志
     * @param cancel           取消下载
     * @param waitDownload     等待下载
     * @param threadNum        所用的线程数，如果是离线网页，则值是-2
     * @param splitStart       分块下载的开始位置
     * @param splitEnd         分块下载的结束位置
     * @param contentLength    文件总大小
     * @param currentLength    已下载的文件大小
     * @param blockSize        分块大小
     * @param downloadSuccess  下载成功的标志
     */
    public DownloadEntity(@NonNull String url, String filename, String path,
                          int blockCompleteNum, int blockPauseNum, String pause,
                          String cancel, String waitDownload,
                          int threadNum, String splitStart, String splitEnd,
                          long contentLength, long currentLength,
                          long blockSize, String downloadSuccess) {
        this.url = url;
        this.filename = filename;
        this.path = path;
        this.blockCompleteNum = blockCompleteNum;
        this.blockPauseNum = blockPauseNum;
        this.pause = pause;
        this.cancel = cancel;
        this.waitDownload = waitDownload;
        this.threadNum = threadNum;
        this.splitStart = splitStart;
        this.splitEnd = splitEnd;
        this.contentLength = contentLength;
        this.currentLength = currentLength;
        this.blockSize = blockSize;
        this.downloadSuccess = downloadSuccess;


    }

    public DownloadEntity(String name, String path, int threadNum) {
        this.filename = name;
        this.path = path;
        this.threadNum = -2;
        this.downloadSuccess="true";
    }

    /*@PrimaryKey(autoGenerate = true)
    public int _id;*/

    @PrimaryKey
    @NonNull
    public String url;//下载地址

    @ColumnInfo(name = "file_name", typeAffinity = ColumnInfo.TEXT)
    public String filename;

    @ColumnInfo(name = "file_path", typeAffinity = ColumnInfo.TEXT)
    public String path;

    @ColumnInfo(name = "blockComplete_num", typeAffinity = ColumnInfo.INTEGER)
    public int blockCompleteNum;

    @ColumnInfo(name = "blockPause_num", typeAffinity = ColumnInfo.INTEGER)
    public int blockPauseNum;

    @ColumnInfo(name = "pause_flag", typeAffinity = ColumnInfo.TEXT, defaultValue = "false")
    public String pause;

    @ColumnInfo(name = "cancel_download", typeAffinity = ColumnInfo.TEXT, defaultValue = "false")
    public String cancel;

    @ColumnInfo(name = "wait_download", typeAffinity = ColumnInfo.TEXT, defaultValue = "false")
    public String waitDownload;

    @ColumnInfo(name = "thread_num", typeAffinity = ColumnInfo.INTEGER)
    public int threadNum;

    @ColumnInfo(name = "split_start")
    public String splitStart;

    @ColumnInfo(name = "split_end")
    public String splitEnd;

    @ColumnInfo(name = "content_len")
    public long contentLength;

    @ColumnInfo(name = "current_Length")
    public long currentLength;

    @ColumnInfo(name = "block_size")
    public long blockSize;

    @ColumnInfo(name = "download_success")
    public String downloadSuccess;

}
