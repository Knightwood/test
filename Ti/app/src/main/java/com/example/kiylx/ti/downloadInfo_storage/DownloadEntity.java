package com.example.kiylx.ti.downloadInfo_storage;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "downloadInfo_tab",indices = {@Index(value = {"url"})})

public class DownloadEntity {

    public DownloadEntity(String url, String filename, String path,
                          int blockCompleteNum, int blockPauseNum, String pause,
                          int threadNum, long contentLength, long totalProcress,
                          long blockSize, String downloadSuccess) {
        this.url = url;
        this.filename = filename;
        this.path = path;
        this.blockCompleteNum = blockCompleteNum;
        this.blockPauseNum = blockPauseNum;
        this.pause = pause;
        this.threadNum = threadNum;
       // this.splitStart = splitStart;long[] splitStart,
        //                          long[] splitEnd,
       // this.splitEnd = splitEnd;
        this.contentLength = contentLength;
        this.totalProcress = totalProcress;
        this.blockSize = blockSize;
        this.downloadSuccess = downloadSuccess;


    }

    @PrimaryKey
    @NonNull
    public String url;//下载地址

    @ColumnInfo(name = "file_name",typeAffinity = ColumnInfo.TEXT)
    public String filename;

    @ColumnInfo(name = "file_path",typeAffinity = ColumnInfo.TEXT)
    public String path;

    @ColumnInfo(name = "blockComplete_num",typeAffinity = ColumnInfo.INTEGER)
    public int blockCompleteNum;

    @ColumnInfo(name = "blockPause_num",typeAffinity = ColumnInfo.INTEGER)
    public int blockPauseNum;

    @ColumnInfo(name = "pause_flag",typeAffinity = ColumnInfo.TEXT,defaultValue = "false")
    public String pause;

    @ColumnInfo(name = "thread_num",typeAffinity = ColumnInfo.INTEGER)
    public int threadNum;

    /*@ColumnInfo(name = "split_start")
    public long[] splitStart;

    @ColumnInfo(name = "split_end")
    public long[] splitEnd;*/

    @ColumnInfo(name = "content_len")
    public long contentLength;

    @ColumnInfo(name = "total_procress")
    public long totalProcress;

    @ColumnInfo(name = "block_size")
    public long blockSize;

    @ColumnInfo(name = "download_success")
    public String downloadSuccess;

}
