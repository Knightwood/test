package com.example.kiylx.ti.DownloadCore;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.example.kiylx.ti.Corebase.DownloadInfo;
import com.example.kiylx.ti.INTERFACE.DownloadMethod;

import java.io.IOException;

/**
 * 下载服务
 */
public class DownloadServices extends Service {
    private DownloadBinder mDownloadBinder;
    private DownloadManager mDownloadManager;

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
    public void onCreate() {
        super.onCreate();
        this.mDownloadBinder=new DownloadBinder();
        mDownloadManager=DownloadManager.getInstance();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public class DownloadBinder extends Binder implements DownloadMethod {

        /**
         * @param info 文件信息
         * @param fileName 修改的文件名称
         * @param filePath 修改的文件存储路径
         * @return 被修改后的文件信息
         */
        @Override
        public DownloadInfo setInfo(DownloadInfo info, String fileName, String filePath){
            info.setFileName(fileName);
            info.setPath(filePath);
            return info;
        }
        @Override
        public void startDownload(DownloadInfo info){
            if(mDownloadManager==null){
                mDownloadManager=DownloadManager.getInstance();
            }
            try{
                mDownloadManager.startDownload(info);
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        @Override
        public void resumeDownload(DownloadInfo info){
            mDownloadManager.resumeDownload(info);
        }
        @Override
        public void pauseDownload(DownloadInfo info){
            mDownloadManager.pauseDownload(info);

        }
        @Override
        public int canaelDownload(DownloadInfo info){
            mDownloadManager.cancelDownload(info);
            return -1;
        }
        @Override
        public long getRate(DownloadInfo info){
           return mDownloadManager.getProgressRate(info);

        }
    }


}
