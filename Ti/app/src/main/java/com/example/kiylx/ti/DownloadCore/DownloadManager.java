package com.example.kiylx.ti.DownloadCore;

import com.example.kiylx.ti.Corebase.DownloadInfo;
import com.example.kiylx.ti.INTERFACE.DOWNLOAD_TASK_FUN;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;;
import java.util.List;


public class DownloadManager {

    private volatile static DownloadManager mDownloadManager;
    private OkhttpManager mOkhttpManager;


    private List<DownloadInfo> readyDownload;
    private List<DownloadInfo> downloading;
    private List<DownloadInfo> pausedownload;

    private int downloadNumLimit=0;

    public static DownloadManager getInstance() {
        if (mDownloadManager == null) {
            synchronized (DownloadManager.class) {
                if (mDownloadManager == null) {
                    mDownloadManager = new DownloadManager();

                }
            }
        }
        return mDownloadManager;
    }

    /**
     *              超过这个限制，把任务加入readydownload
     */
    private DownloadManager() {
        //获取配置文件里的下载数量限制，赋值给downloadNumLimit
        //还没写配置文件，这里用5暂时代替
        downloadNumLimit=5;

        downloading = new ArrayList<>();

        pausedownload = new ArrayList<>();
        readyDownload = new ArrayList<>();
        mOkhttpManager = OkhttpManager.getInstance();

    }

    /**
     * 线程中执行的任务，在遇到下载成功，失败，取消，暂停，更新进度时所使用
     */
    private DOWNLOAD_TASK_FUN mTASK_fun=new DOWNLOAD_TASK_FUN() {

        /**
         * @param info 下载信息
         * @return
         * 每条线程都会调用，那么每次一个线程调用，当threaduse累加等于线程数时
         * 文件的多个线程下载都已经暂停，把其从正在下载中移除，放入暂停下载
         */
        @Override
        public synchronized boolean pausedDownload(DownloadInfo info) {
            int i=info.setThreadUse(1);
            if (i==info.getThreadNum()){
                downloading.remove(info);
                pausedownload.add(info);
            }
            return true;
        }

        /**
         * @param info 下载信息
         * @return
         * 每条线程都会调用，那么每次一个线程调用，当threaduse累加等于线程数时
         * 文件的多个线程下载都已经取消，此时删除文件
         */
        @Override
        public synchronized boolean canceledDownload(DownloadInfo info) {

            int i=info.setThreadUse(1);
            if (i==info.getThreadNum()){
                downloading.remove(info);
                //删除文件
                File file=new File(info.getPath()+info.getFileName());
                if (file.exists()&&file.isFile()){
                    file.delete();
                }
            }
            return true;
        }

        /**
         * @param info 下载信息
         * @return
         * 每一个线程完成下载都会使完成数量加一，标志着文件块的下载成功，累计为线程数个数的文件块下载完成，下载成功。
         * completeNum不应被清零。
         */
        @Override
        public synchronized boolean downloadSucess(DownloadInfo info) {

            info.setCompleteNum();
            if (info.getCompleteNum()==info.getThreadNum()){
                //下载完成，触发通知
                //并且，把downloading中的此downloadinfo移除，且把完成信息放入“持久化存储”
                //调入新的下载任务开始下载
                if (!readyDownload.isEmpty()){
                    try {
                        startDownload(readyDownload.get(0));
                    }catch (IOException e){
                        e.printStackTrace();
                    }

                }
            }else {
                return false;
            }
            System.out.println("下载成功");
            return true;
        }
    };

    /**
     * @param info downloadinfo
     * @return 加工信息，生成文件分块下载信息等。
     */
    private void processInfo(DownloadInfo info) throws IOException {
        //用于文件下载的线程数
        int threadNum = info.getThreadNum();
        info.setFileLength(mOkhttpManager.getFileLength(info.getUrl()));
        System.out.println(" 文件总大小"+info.getFileLength());

        //文件的分块大小
        long blocksize = info.getFileLength() / threadNum;
        info.setBlockSize(blocksize);

        //建立数组准备存储分块信息
        info.splitEnd = new long[threadNum];
        info.splitStart = new long[threadNum];
//开始块和结束块。
        for (int i = 0; i < threadNum; i++) {
            info.splitStart[i] = i * blocksize;
            info.splitEnd[i] = (i + 1) * blocksize - 1;
        }
        info.splitEnd[threadNum-1]=info.getFileLength();
    }

    /**
     * @param info 文件下载信息
     * @throws IOException
     */
    public void startDownload(DownloadInfo info) throws IOException {

        //限制下载，超过限制的放入readyDownload的零号位置，等当前下载任务完成再从0位置取出来下载
        if (downloading.size()==downloadNumLimit){
            //加入准备下载
            readyDownload.add(0,info);
        }else {
            //处理信息
            processInfo(info);

            //File file = new File(info.getPath() + info.getFileName());

            int i = info.getThreadNum();
            for (int j = 0; j < i; j++) {
                TaskPool.getInstance().executeTask(new DownloadTaskRunnable(info, j,mTASK_fun));
            }

            //加入下载队列
            downloading.add(info);
        }

        //重置threadUse
        info.setThreadUse(0);

    }

    public void pauseDownload(DownloadInfo info) {
        info.setPause(true);
    }

    public void cancelDownload(DownloadInfo info) {
        info.setCancel(true);


    }

    /**
     * @param info 下载信息
     * @return 返回下载进度
     * 文件的下载线程数就是文件分块的标号，
     * 那么分块的结束减去分块的开始就是未下载的部分
     */
    public long getProgressRate(DownloadInfo info){
        long unDownloadPart=0;
        for (int i = 0; i < info.getThreadNum(); i++) {
           unDownloadPart+= info.splitEnd[i]-info.splitStart[i];
        }
        return 1-(unDownloadPart/info.getFileLength());
    }

}
