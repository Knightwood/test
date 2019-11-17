package com.example.kiylx.ti.Corebase;

public class DownloadInfo {
    private String url;
    private String fileName;
    private String path;

    private boolean pause=false;
    private boolean cancel=false;

    private int threadNum;

    public long splitStart[]=null;
    public long splitEnd[]=null;

    private long fileLength;
    /**
     * 当前已下载的大小
     */
    private long totalProcress;

    /**
     * @param url 下载地址
     * @param path 下载到存储的路径
     * @param fileName 文件名称，可以从url中获取也可以被修改
     * @param threadNum 下载这个文件的线程数
     *                  rangeStart和rangeEnd由线程数和文件长度计算得来
     */
    public DownloadInfo(String url, String path,String fileName, int threadNum){
        this.url=url;
        this.path = path;
        this.fileName=fileName;
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
}
