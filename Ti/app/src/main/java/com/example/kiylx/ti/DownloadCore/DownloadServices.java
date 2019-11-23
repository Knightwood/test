package com.example.kiylx.ti.DownloadCore;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.example.kiylx.ti.Corebase.DownloadInfo;
import com.example.kiylx.ti.activitys.DownloadActivity;

import java.io.IOException;

public class DownloadServices extends Service {
    private DownloadBinder mDownloadBinder=new DownloadBinder();
    private DownloadManager mDownloadManager;

    @Override
    public void onCreate() {
        super.onCreate();
        mDownloadManager=DownloadManager.getInstance();

    }

    public class DownloadBinder extends Binder{

        /**
         * @param info 文件信息
         * @param fileName 修改的文件名称
         * @param filePath 修改的文件存储路径
         * @return 被修改后的文件信息
         */
        public DownloadInfo setInfo(DownloadInfo info, String fileName, String filePath){
            info.setFileName(fileName);
            info.setPath(filePath);
            return info;
        }

        public void startDownload(DownloadInfo info){
            try{
                mDownloadManager.startDownload(info);
            }catch (IOException e){
                e.printStackTrace();
            }

        }
        public void resumeDownload(DownloadInfo info){
            mDownloadManager.resumeDownload(info);
        }
        public void pauseDownload(DownloadInfo info){
            mDownloadManager.pauseDownload(info);

        }
        public int canaelDownload(DownloadInfo info){
            mDownloadManager.cancelDownload(info);
            return -1;
        }
        public long getRate(DownloadInfo info){
           return mDownloadManager.getProgressRate(info);

        }
    }



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mDownloadBinder;
    }
}
