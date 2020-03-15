package com.example.kiylx.ti.downloadCore;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.example.kiylx.ti.corebase.DownloadInfo;
import com.example.kiylx.ti.myInterface.DownloadClickMethod;
import com.example.kiylx.ti.myInterface.DownloadMethod;

import java.io.IOException;

/**
 * 下载服务
 */
public class DownloadServices extends Service {
    private DownloadBinder mDownloadBinder;
    private DownloadManager mDownloadManager;
    private DownloadClickMethod controlMethod;

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
        public int getDownloadingNum() {
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
         * @return 返回控制下载任务的接口
         * <p>
         * 下载任务条目会使用这些接口控制下载过程，而这些接口调用的是downloadmanager中的方法
         */
        public DownloadClickMethod getInferface() {
            if (controlMethod == null) {
                controlMethod = new DownloadClickMethod() {
                    @Override
                    public void download(DownloadInfo info) {
                        startDownload(info);
                    }

                    @Override
                    public void pause(DownloadInfo info) {
                        mDownloadManager.pauseDownload(info);
                    }

                    @Override
                    public void cancel(DownloadInfo info) {
                        mDownloadManager.cancelDownload(info);
                    }

                    @Override
                    public void reasume(DownloadInfo info) {
                        mDownloadManager.resumeDownload(info);
                    }

                    @Override
                    public float getPercent(DownloadInfo info) {
                        return mDownloadManager.getPercentage(info);
                    }
                };

            }
            return controlMethod;
        }


        public void resumeAll() {
            mDownloadManager.resumeAll();
        }

        public void cancelAll() {
            mDownloadManager.canaelAll();
        }

        public void pauseAll() {
            mDownloadManager.pauseAll();
        }
    }

}
