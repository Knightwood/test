package com.example.kiylx.ti.Corebase;

import android.os.Environment;

public class DownloadInfo {
    private String url;
    private String fileName;
    private String path;
    /**
     * 每个线程下载完成后会使其+1，如此，在下载管理中就可以知道，若
     * completeNum等于threadNum，则，下载完成。这里所用来判断的threadNum是downloadinfo中的，为防止
     * 在设置中更改下载线程数量而导致出错
     */
    private int completeNum=0;
    /**
     * 这个变量用于下载暂停时统计线程数量，
     * 达到所用的线程数量就意味着这个文件的下载线程就都暂停了，可以进行其他的操作。
     */
    private int threadUse;

    private boolean pause=false;
    private boolean cancel=false;
    /**
     * 所用多少线程下载，若当前任务正在下载，那设置中更改线程数量不会被应用到这个正在下载或
     * 正在暂停状态的任务
     */
    private int threadNum;

    public long splitStart[]=null;
    public long splitEnd[]=null;

    /**
     * 需要下载文件的总大小，其从response中获取
     */
    private long fileLength;
    /**
     * 当前已下载的大小
     */
    private long totalProcress;

    /**
     * 下载过程中文件分块大小
     */
    private long blockSize;

    /**
     * @param url 下载地址
     * @param path 下载到存储的路径
     * @param fileName 文件名称，可以从url中获取也可以被修改
     * @param threadNum 下载这个文件的线程数
     *                  rangeStart和rangeEnd由线程数和文件长度计算得来
     */
    public DownloadInfo(String url, String path,String fileName, int threadNum){
        this.url=url;

        if (fileName == null) {
            this.fileName = url.substring(url.lastIndexOf("/"));
        }
        if (this.path == null) {
            //默认路径
            this.path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
        }

        this.threadNum=threadNum;
        //初始化暂停和取消的标志
        this.pause=false;
        this.cancel=false;
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

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }


    public double getTotalProcress() {
        return totalProcress;
    }

    public void setTotalProcress(long totalProcress) {
        this.totalProcress = totalProcress;
    }

    public long getFileLength() {
        return fileLength;
    }

    public void setFileLength(long fileLength) {
        this.fileLength = fileLength;
    }


    public int getThreadNum() {
        return threadNum;
    }

    public void setThreadNum(int threadNum) {
        this.threadNum = threadNum;
    }

    public boolean isPause() {
        return pause;
    }

    public void setPause(boolean pause) {
        this.pause = pause;
    }

    public boolean isCancel() {
        return cancel;
    }

    public void setCancel(boolean cancel) {
        this.cancel = cancel;
    }

    public int getCompleteNum() {
        return completeNum;
    }

    public void setCompleteNum() {
        this.completeNum+=1;
    }

    public long getBlockSize() {
        return blockSize;
    }

    public void setBlockSize(long blockSize) {
        this.blockSize = blockSize;
    }

    public int setThreadUse(int i) {
        if (i==0){
            this.threadUse=0;
        }else
        this.threadUse += 1;
        return this.threadUse;
    }
}
