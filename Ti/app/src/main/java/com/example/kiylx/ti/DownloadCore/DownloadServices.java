package com.example.kiylx.ti.DownloadCore;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.example.kiylx.ti.Corebase.DownloadInfo;

public class DownloadServices extends Service {
    private DownloadBinder mDownloadBinder=new DownloadBinder();
    private DownloadManager mDownloadManager;

    @Override
    public void onCreate() {
        super.onCreate();
        mDownloadManager=DownloadManager.getInstance();

    }

    class DownloadBinder extends Binder{

        /**
         * @param url 下载地址
         * @return 返回由下载地址生成的默认文件信息
         */
        public DownloadInfo getINfo(String url){
            return mDownloadManager.generateDownloadInfo(url,null,null,8);
        }

        /**
         * @param info 文件信息
         * @param fileName 修改的文件名称
         * @param filePath 修改的文件存储路径
         * @return 被修改后的文件信息
         */
        public DownloadInfo setInfo(DownloadInfo info,String fileName,String filePath){
            info.setFileName(fileName);
            info.setPath(filePath);
            return info;
        }

        public void startDownload(DownloadInfo info){

        }
        public void pauseDownload(DownloadInfo info){

        }
        public int canaelDownload(DownloadInfo info){
            return -1;
        }
    }



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mDownloadBinder;
    }
}
