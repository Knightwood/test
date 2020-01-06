package com.example.kiylx.ti.DownloadCore;

import android.content.Context;
import android.util.Log;

import androidx.databinding.DataBindingUtil;
import androidx.room.Room;

import com.example.kiylx.ti.Corebase.DownloadInfo;
import com.example.kiylx.ti.DownloadInfo_storage.DatabaseUtil;
import com.example.kiylx.ti.DownloadInfo_storage.DownloadEntity;
import com.example.kiylx.ti.DownloadInfo_storage.DownloadInfoDatabase;
import com.example.kiylx.ti.INTERFACE.DOWNLOAD_TASK_FUN;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;;
import java.util.List;

public class DownloadManager {
    private static final String TAG="下载管理器";

    private volatile static DownloadManager mDownloadManager;
    private OkhttpManager mOkhttpManager;
    //private DownloadInfoDatabase mDatabase;//下载信息数据库


    private List<DownloadInfo> readyDownload;
    private List<DownloadInfo> downloading;
    private List<DownloadInfo> pausedownload;

    private int downloadNumLimit = 0;
    private Context mContext;

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
     * 超过这个限制，把任务加入readydownload
     */
    private DownloadManager() {
        //获取配置文件里的下载数量限制，赋值给downloadNumLimit
        //还没写配置文件，这里用5暂时代替
        downloadNumLimit = 5;

        downloading = new ArrayList<>();
        pausedownload = new ArrayList<>();
        readyDownload = new ArrayList<>();
        mOkhttpManager = OkhttpManager.getInstance();

    }

    /**
     * 线程中执行的任务，在遇到下载成功，失败，取消，暂停，更新进度时所使用
     */
    private DOWNLOAD_TASK_FUN mTASK_fun = new DOWNLOAD_TASK_FUN() {

        /**
         * @param info 下载信息
         * @return
         * 每条线程都会调用，那么每次一个线程调用，当threaduse累加等于线程数时
         * 文件的多个线程下载都已经暂停，把其从正在下载中移除，放入暂停下载
         */
        @Override
        public synchronized boolean downloadPaused(DownloadInfo info) {
            info.setblockPauseNum(1);
            //所有下载文件的线程已经暂停
            if (info.getblockPauseNum()) {
                //如果文件取消下载
                if (info.isCancel()) {
                    downloading.remove(info);
                    deleteFile(info);
                } else {
                    //如果只是暂停
                    downloading.remove(info);
                    pausedownload.add(info);
                }
            }
            Log.d(TAG, "下载暂停成功");
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
            if (info.isDownloadSuccess()) {
                //下载完成，触发通知
                //并且，把downloading中的此downloadinfo移除，且把完成信息放入“持久化存储”
                //调入新的下载任务开始下载
                if (!readyDownload.isEmpty()) {
                    try {
                        startDownload(readyDownload.get(0));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            } else {
                return false;
            }
            System.out.println("下载成功");
            return true;
        }
    };
//--------------------------------------以上是接口方法多线程会调用它，以下是一些下载方法------------------------------//

    /**
     * @param info downloadinfo
     * @return 加工信息，生成文件分块下载信息等。
     */
    private void processInfo(DownloadInfo info) throws IOException {
        //用于文件下载的线程数
        int threadNum = info.getThreadNum();
        //如果下载文件的大小已经给出了（也就是info里contentLength不等于0），就直接赋值。否则，调用getFileLength获取
        info.setContentLength(info.getContentLength() == 0 ? mOkhttpManager.getFileLength(info.getUrl()) : info.getContentLength());
        System.out.println(" 文件总大小" + info.getContentLength());

        //文件的分块大小
        long blocksize = info.getContentLength() / threadNum;
        info.setBlockSize(blocksize);

        //建立数组准备存储分块信息
        info.splitEnd = new long[threadNum];
        info.splitStart = new long[threadNum];
//开始块和结束块。
        for (int i = 0; i < threadNum; i++) {
            info.splitStart[i] = i * blocksize;
            info.splitEnd[i] = (i + 1) * blocksize - 1;
        }
        info.splitEnd[threadNum - 1] = info.getContentLength();
    }

    /**
     * @param info 文件下载信息
     * @throws IOException
     */
    public void startDownload(DownloadInfo info) throws IOException {
        if (info.isResume()) {
            int i = info.getThreadNum();
            for (int j = 0; j < i; j++) {
                TaskPool.getInstance().executeTask(new DownloadTaskRunnable(info, j, mTASK_fun));
            }
        } else {
            //全新开始的下载
            //限制下载，超过限制的放入readyDownload的零号位置，等当前下载任务完成再从0位置取出来下载
            if (downloading.size() == downloadNumLimit) {
                //加入准备下载
                readyDownload.add(0, info);
            } else {
                //处理信息
                processInfo(info);

                int i = info.getThreadNum();
                for (int j = 0; j < i; j++) {
                    TaskPool.getInstance().executeTask(new DownloadTaskRunnable(info, j, mTASK_fun));
                }

                //加入下载队列
                downloading.add(info);
            }
        }

        //重置threadUse
        info.setblockPauseNum(0);

    }

    /**
     * @param info 下载信息
     *             修改下载信息的暂停标记，使得下载文件的所有线程暂停文件块的下载
     *             传入为null则遍历所有正在下载的进行暂停
     */
    public void pauseDownload(DownloadInfo info) {
        if (info==null){
            for (int i = 0; i <downloading.size(); i++) {
                downloading.get(i).setPause(true);
            }
        }else
        info.setPause(true);
    }

    /**
     * @param info 下载信息
     *             修改下载信息的取消下载标记，使得下载文件的所有线程取消文件块的下载
     *             取消下载的流程实现暂停，然后再删除文件
     */
    public void cancelDownload(DownloadInfo info) {
        info.setCancel(true);
        pauseDownload(info);

    }

    /**
     * @param info 下载信息
     *             删除文件
     */
    private void deleteFile(DownloadInfo info) {
        File file = new File(info.getPath() + info.getFileName());
        if (file.exists() && file.isFile()) {
            file.delete();
        }
        //+还要根据cancel标记决定是否要删除记录
    }

    /**
     * @param info 下载信息
     *             继续下载
     *             判断downloading列表是否满了，如果满了，放进ready列表
     */
    public void resumeDownload(DownloadInfo info) {
        info.setPause(false);//暂停标志设为假

        pausedownload.remove(info);//移除暂停列表中的这个条目
        //如果正在下载列表满了，把这个条目加入准备列表。否则加入正在下载列表开始下载
        if (downloading.size() == downloadNumLimit) {
            readyDownload.add(info);
        } else {
            downloading.add(info);
            try {
                startDownload(info);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * @param info 下载信息
     * @return 返回下载进度
     * 文件的下载线程数就是文件分块的标号，
     * 那么分块的结束减去分块的开始就是未下载的部分
     */
    public long getProgressRate(DownloadInfo info) {
        long unDownloadPart = 0;
        for (int i = 0; i < info.getThreadNum(); i++) {
            unDownloadPart += info.splitEnd[i] - info.splitStart[i];
        }
        return 1 - (unDownloadPart / info.getContentLength());
    }

    public void setContext(Context context) {
        this.mContext = context;
    }
    /*废弃
    private void getDatabase() {
        mDatabase = DownloadInfoDatabase.getInstance(mContext);
    }*/

    private void insertData(DownloadInfo info) {
        DatabaseUtil.getDao(mContext).insertAll();
    }

    private void update() {
        DatabaseUtil.getDao(mContext).update();
    }

    private void delete() {
        //DatabaseUtil.getDao(mContext).delete(info);
    }

}
