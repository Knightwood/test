package com.example.kiylx.ti.downloadCore;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.example.kiylx.ti.corebase.DownloadInfo;
import com.example.kiylx.ti.myInterface.DownloadMethod;

import java.io.IOException;

/**
 * 下载服务
 */
public class DownloadServices extends Service {
    private DownloadBinder mDownloadBinder;
    private DownloadManager mDownloadManager;

    @Override
    public void onCreate() {
        super.onCreate();
        this.mDownloadBinder = new DownloadBinder();
        mDownloadManager = DownloadManager.getInstance();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mDownloadBinder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public class DownloadBinder extends Binder {

        /**
         * @return 返回正在下载的个数
         */
        public int getDownloadingNum(){
            return mDownloadManager.getDownloadingNum();
        }

        /**
         * @return 返回暂停下载的个数
         */
        public int getPauseNum() {
            return mDownloadManager.getPauseNum();
        }

        /**
         * @param info     文件信息
         * @param fileName 修改的文件名称
         * @param filePath 修改的文件存储路径
         * @return 被修改后的文件信息
         */

        public DownloadInfo setInfo(DownloadInfo info, String fileName, String filePath) {
            info.setFileName(fileName);
            info.setPath(filePath);
            return info;
        }


        public void startDownload(DownloadInfo info) {
            if (mDownloadManager == null) {
                mDownloadManager = DownloadManager.getInstance();
            }
            try {
                mDownloadManager.startDownload(info);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        /**
         * 开始全部的下载任务
         */



        public void resumeDownload(DownloadInfo info) {
            mDownloadManager.resumeDownload(info);
        }


        public void resumeAll() {
            mDownloadManager.resumeAll();
        }


        public void pauseDownload(DownloadInfo info) {
            mDownloadManager.pauseDownload(info);
        }


        public void pauseAll() {
            mDownloadManager.pauseAll();
        }


        public void canaelDownload(DownloadInfo info) {
            mDownloadManager.cancelDownload(info, false);

        }


        public void cancelAll() {
            mDownloadManager.canaelAll();
        }


        public float getRate(DownloadInfo info) {
            return mDownloadManager.getPercentage(info);

        }
    }


}
