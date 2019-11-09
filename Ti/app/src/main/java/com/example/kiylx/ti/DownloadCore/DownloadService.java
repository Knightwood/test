package com.example.kiylx.ti.DownloadCore;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;

import java.io.File;

public class DownloadService extends Service {
    DownloadTask mDownloadTask;
    String downloadUrl;

    public DownloadService() {
    }

    private DownloadListener mDownloadListener=new DownloadListener() {
        @Override
        public void onProgress(int progress) {

        }

        @Override
        public void onSuccess() {

        }

        @Override
        public void onFailed() {

        }

        @Override
        public void onPaused() {

        }

        @Override
        public void onCanceled() {

        }
    };

    public class DownloadBinder extends Binder {

        public DownloadService getService(){
            return DownloadService.this;
        }

        public void startDownload(String url){
            if (mDownloadTask==null){
                downloadUrl =url;
                mDownloadTask=new DownloadTask(mDownloadListener);
                mDownloadTask.execute(downloadUrl);
            }
        }

        public void pauseDownload(){
            if (mDownloadTask!=null){
                mDownloadTask.pauseDownload();
            }
        }

        public void cancelDownload(){
            if (mDownloadTask!=null){
                mDownloadTask.canaelDownload();
            }else {
                if (downloadUrl !=null){
                    // 取消下载时需将文件删除，并将通知关闭
                    String fileName = downloadUrl.substring(downloadUrl.lastIndexOf("/"));
                    String directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
                    File file = new File(directory + fileName);
                    if (file.exists()) {
                        file.delete();
                    }

                    //通知
                }
            }
        }
    }

    private  DownloadBinder mBinder=new DownloadBinder();

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.

        return mBinder;
    }
}
