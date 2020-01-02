package com.example.kiylx.ti.DownloadInfo_storage;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "downloadInfo_tab")

public class DownloadEntity {

    public DownloadEntity(String url, String filename, String path,
                          int blockCompleteNum, int blockPauseNum, boolean pause,
                          boolean resume, int threadNum, long[] splitStart,
                          long[] splitEnd, long contentLength, long totalProcress,
                          long blockSize, boolean downloadSuccess) {
        this.url = url;
        this.filename = filename;
        this.path = path;
        this.blockCompleteNum = blockCompleteNum;
        this.blockPauseNum = blockPauseNum;
        this.pause = pause;
        this.resume = resume;
        this.threadNum = threadNum;
        this.splitStart = splitStart;
        this.splitEnd = splitEnd;
        this.contentLength = contentLength;
        this.totalProcress = totalProcress;
        this.blockSize = blockSize;
        this.downloadSuccess = downloadSuccess;


    }

    @PrimaryKey
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
    public boolean pause;

    @ColumnInfo(name = "resume_flag")
    public boolean resume;

    @ColumnInfo(name = "thread_num",typeAffinity = ColumnInfo.INTEGER)
    public int threadNum;

    @ColumnInfo(name = "split_start")
    public long[] splitStart;

    @ColumnInfo(name = "split_end")
    public long[] splitEnd;

    @ColumnInfo(name = "content_len")
    public long contentLength;

    @ColumnInfo(name = "total_procress")
    public long totalProcress;

    @ColumnInfo(name = "block_size")
    public long blockSize;

    @ColumnInfo(name = "download_success")
    public boolean downloadSuccess;

}
