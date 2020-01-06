package com.example.kiylx.ti.corebase;

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
    private int blockCompleteNum = 0;
    /**
     * 这个变量用于下载暂停时统计线程数量，
     * 达到下载文件分配的线程数量（threadNum）就意味着这个文件的下载线程就都暂停了，可以进行其他的操作。
     */
    private int blockPauseNum;

    /**
     * 当前文件下载是否已暂停
     * “暂停标志”为 真，则“恢复标志”为 假。
     * pause和resume这两个互斥，暂停时不会是恢复状态，恢复时不会是暂停状态。
     * 不设resume标志，用" !pause "表示resume标志
     */
    private boolean pause = false;
    /**
     * 当前文件下载是否已取消
     */
    private boolean cancel = false;

    /**
     * 下载这个文件而分配的线程数量。
     * 若当前任务正在下载，那设置中更改线程数量不会被应用到这个正在下载或
     * 正在暂停状态的任务
     */
    private int threadNum;

    /**
     * 下载时根据分配线程数量（threadNum）决定的文件分块大小
     */
    private long blockSize;
    public long[] splitStart = null;
    public long[] splitEnd = null;

    /**
     * 需要下载文件的总大小，其从response中获取
     * 或者从调用它的方法中给予
     */
    private long contentLength;
    /**
     * 当前总文件已下载的大小
     */
    private long totalProcress;

    /**
     * 是否下载完成的标记
     */
    private boolean downloadSuccess;


    /**
     * @param url       下载地址
     * @param path      下载到存储的路径
     * @param fileName  文件名称，可以从url中获取也可以被修改
     * @param threadNum 下载这个文件的线程数
     *                  rangeStart和rangeEnd由线程数和文件长度计算得来
     */
    public DownloadInfo(String url, String path, String fileName, int threadNum, long contentLength) {
        this.url = url;

        if (fileName == null) {
            this.fileName = url.substring(url.lastIndexOf("/"));
        }
        if (this.path == null) {
            //默认路径
            this.path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
        }
        this.contentLength = contentLength;

        this.threadNum = threadNum;
        //初始化暂停和取消的标志
        this.pause = false;
        this.cancel = false;
    }

    /**
     * 只有一个下载地址的构造函数
     */
    public DownloadInfo(String url) {
        this(url, null, null, 8, 0);
    }

    /**
     * @param url           下载地址
     * @param contentLength 下载文件的文件长度
     */
    public DownloadInfo(String url, long contentLength) {
        this(url, null, null, 8, contentLength);
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

    public long getContentLength() {
        return contentLength;
    }

    public void setContentLength(long contentLength) {
        this.contentLength = contentLength;
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

    public int getBlockCompleteNum() {
        return blockCompleteNum;
    }

    /**
     * manager中。每个文件的每个下载分块线程在下载完成后会调用这个方法，
     * 当completeNum增加到下载文件所分配的线程数的时候，意味着所有分块已经下载完成。
     * 可以将downloadSuccess标记为true
     */
    public void setCompleteNum() {

        if ((this.blockCompleteNum += 1) == this.threadNum) {

            setSuccessFlags(true);
        }
    }

    public long getBlockSize() {
        return blockSize;
    }

    public void setBlockSize(long blockSize) {
        this.blockSize = blockSize;
    }

    /**
     * @return 真或假
     * 所有分块，下载暂停时都会会累加blockPauseNum，
     * 直到等于文件下载所分配的线程数时返回true
     */
    public boolean getblockPauseNum() {
        return this.blockPauseNum == this.threadNum;
    }

    /**
     * @param i 标记是否重置，若是0以外的数字，把1累加给blockPauseNum
     */
    public void setblockPauseNum(int i) {
        if (i == 0) {
            this.blockPauseNum = 0;
        } else
            this.blockPauseNum += 1;
    }

    public boolean isDownloadSuccess() {
        return downloadSuccess;
    }

    private void setSuccessFlags(boolean flag) {
        this.downloadSuccess = flag;
    }
    /*
    public void setResume(boolean b){
        this.resume=b;
    }
    public boolean isResume() {
        return resume;
    }*/
}
