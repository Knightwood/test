package com.crystal.fucktoollibrary.tools.download;

import androidx.annotation.NonNull;


import com.crystal.fucktoollibrary.tools.SomeRes;

import java.util.UUID;

/**
 * pause 暂停标志
 * <p>
 * waitDownload 等待下载标志
 * <p>
 * 非暂停恢复下载;非暂停等待下载：暂停为假,准备下载为真
 * <p>
 * 暂停状态：暂停为真，准备下载为假
 * <p>
 * 构建的下载任务：暂停是假，准备下载是假
 * <p>
 * 正在下载：暂停下载为假，准备下载为假
 */
public class DownloadInfo {
    private String uuid;//标识唯一信息
    private String url;
    private String fileName;
    private String path;
    /**
     * 每个线程下载完成后会使其+1，如此，在下载管理中就可以知道，若
     * completeNum等于threadNum，则，下载完成。这里所用来判断的threadNum是downloadinfo中的，为防止
     * 在设置中更改下载线程数量而导致出错
     */
    private volatile int blockCompleteNum = 0;
    /**
     * 这个变量用于下载暂停时统计线程数量，
     * 达到下载文件分配的线程数量（threadNum）就意味着这个文件的下载线程就都暂停了，可以进行其他的操作。
     */
    private volatile int blockPauseNum;

    /**
     * 当前文件下载是否已暂停
     * “暂停标志”为 真，则“恢复标志”为 假。
     * pause和resume这两个互斥，暂停时不会是恢复状态，恢复时不会是暂停状态。
     * 不设resume标志，用" !pause "表示resume标志
     */
    private volatile boolean pause = false;
    /**
     * 当前文件下载是否已取消
     */
    private boolean cancel = false;
    /**
     * 文件从暂停状态到下载状态时会因为下载数量限制放进准备下载列表中，
     * 所以放置一个waitDownload标志，表示不是暂停，正等待下载。
     */
    private boolean waitDownload = false;

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
     * 当前文件已下载的大小,
     * 需要百分比的话，用contentLength和totalLength自行计算，结果要转成float类型
     */
    private long currentLength;

    /**
     * 是否下载完成的标记
     */
    private volatile boolean downloadSuccess;


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
            this.fileName = url.substring(url.lastIndexOf("/"));//斜杠不能丢，因为是路径加文件名，如果文件名不包含“/”，那路径会不正确会出错
        }
        this.path = path;

        this.contentLength = contentLength;

        this.threadNum = threadNum;
        //初始化暂停,等待，和取消的标志
        this.pause = false;
        this.cancel = false;
        this.waitDownload = false;
        if (this.uuid == null) {
            uuid = UUID.randomUUID().toString();
        }
    }

    /**
     * 数据库entity转换成downloadinfo
     * @param url
     * @param filename
     * @param path
     * @param blockCompleteNum
     * @param blockPauseNum
     * @param pause
     * @param cancel
     * @param waitDownload
     * @param threadNum
     * @param splitStart
     * @param splitEnd
     * @param contentLength
     * @param currentLength
     * @param blockSize
     * @param downloadSuccess
     * @param uuid
     */
    public DownloadInfo(@NonNull String url, String filename, String path,
                        int blockCompleteNum, int blockPauseNum, boolean pause,
                        boolean cancel, boolean waitDownload,
                        int threadNum, long[] splitStart, long[] splitEnd,
                        long contentLength, long currentLength,
                        long blockSize, boolean downloadSuccess, @NonNull String uuid) {
        this.url = url;
        this.fileName = filename;
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
        this.uuid = uuid;
    }

    /**
     * 只有一个下载地址的构造函数
     */
    public DownloadInfo(String url) {
        this(url, null, null, SomeRes.downloadThreadNum, 0);
    }

    /**
     * @param url           下载地址
     * @param contentLength 下载文件的文件长度
     */
    public DownloadInfo(String url, long contentLength) {
        this(url, null, null, SomeRes.downloadThreadNum, contentLength);
    }

    /**
     * @param url           下载地址
     * @param contentLength 下载文件的文件长度
     * @param threadNum     下载线程数
     */
    public DownloadInfo(String url, long contentLength, int threadNum) {
        this(url, null, null, threadNum, contentLength);
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


    public long getCurrentLength() {
        long unDownloadPart = 0;//未下载的部分
        for (int i = 0; i < this.getThreadNum(); i++) {
            unDownloadPart += (this.splitEnd[i] - this.splitStart[i] + 1);
        }
        //设置已下载的长度
        this.setCurrentLength(this.getContentLength() - unDownloadPart);
        return currentLength;
    }

    public void setCurrentLength(long currentLength) {
        this.currentLength = currentLength;
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

    public int setThreadNum(int threadNum) {
        this.threadNum = threadNum;
        return threadNum;
    }

    public boolean isPause() {
        return pause;
    }

    /**
     * 存进数据库时所需的转换操作，把真假的布尔值转换为string值
     */
    /*public String getPauseFlags() {
        return isPause() ? "true" : "false";
    }*/
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
    public boolean getblockPause() {
        return this.blockPauseNum == this.threadNum;
    }

    /**
     * @return 返回暂停分块数量
     */
    public int getBlockPauseNum() {
        return this.blockPauseNum;
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

    public boolean isWaitDownload() {
        return waitDownload;
    }

    public void setWaitDownload(boolean waitDownload) {
        this.waitDownload = waitDownload;
    }

    /**
     * @return 返回下载进度, float类型
     * 文件的下载线程数就是文件分块的标号，
     * 那么分块的结束减去分块的开始就是未下载的部分
     */
    public float getPercent() {
        //返回已下载百分比
        return ((float) this.getCurrentLength() / (float) this.getContentLength());
    }

    public int getIntPercent() {
        return (int) (((float) this.getCurrentLength() / (float) this.getContentLength()) * 100);
    }

    public String getUuid() {
        if (this.uuid==null){
           this.uuid=UUID.randomUUID().toString();
        }
            return this.uuid;
    }
}
