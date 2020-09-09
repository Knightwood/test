package com.example.kiylx.ti.downloadpack.downloadcore;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.example.kiylx.ti.downloadpack.core.DownloadInfo;
import com.example.kiylx.ti.downloadpack.dinterface.ControlDownloadMethod;
import com.example.kiylx.ti.downloadpack.dinterface.SendData;
import com.example.kiylx.ti.tool.threadpool.SimpleThreadPool;
import com.example.kiylx.ti.tool.threadpool.SimpleThreadPool2;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 下载服务,作为downloadManager的代理，以及保持下载管理器不会被杀死
 */
public class DownloadServices extends Service {
    private DownloadBinder mDownloadBinder;
    private SimpleDownlaodManager mDownloadManager;
    private ControlDownloadMethod controlMethod;

    @Override
    public void onCreate() {
        super.onCreate();
        this.mDownloadBinder = new DownloadBinder();
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
        stopSelf();
    }

    public class DownloadBinder extends Binder {

        public void newDownloadManager(SendData mInterface) {
            mDownloadManager = SimpleDownlaodManager.newInstance(
                    null,
                    SimpleThreadPool.getInstance().getExecutorService(),
                    SimpleThreadPool2.getInstance().getExecutorService(),
                    3,
                    mInterface
            );
            if (mInterface != null)
                mInterface.notifyUpdate();//实例存在，像持有者推送数据
        }

        public void setGetDataInterface(SendData dataInterface) {
            if (mDownloadManager != null) {
                mDownloadManager.setSendDataInterface(dataInterface);
            } else {
                newDownloadManager(dataInterface);
            }
        }

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

        public void startDownload(DownloadInfo info) throws Exception {
            if (mDownloadManager == null) {
                throw new Exception("未调用newDownloadManager(SendData mInterface)生成downloadmanager");
            }
            try {
                mDownloadManager.startDownload(info, false);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /**
         * @return 返回控制下载任务的接口
         * <p>
         * 下载任务条目会使用这些接口控制下载过程，而这些接口调用的是downloadmanager中的方法
         */
        public ControlDownloadMethod getInferface() {
            if (controlMethod == null) {
                controlMethod = new ControlDownloadMethod() {
                    @Override
                    public void download(DownloadInfo info) throws Exception {
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

                    @Override
                    public List<DownloadInfo> getAllDownload() {
                        List<DownloadInfo> list = new ArrayList<>();
                        list.addAll(mDownloadManager.getDownloading());
                        list.addAll(mDownloadManager.getPausedownload());
                        list.addAll(mDownloadManager.getReadyDownload());
                        return list;
                    }

                    @Override
                    public List<DownloadInfo> getAllComplete() {
                        List<DownloadInfo> list = new ArrayList<>();
                        list.addAll(mDownloadManager.getCompleteDownload());
                        return list;
                    }

                    @Override
                    public void getAllDownload(List<DownloadInfo> list) {
                        list.clear();
                        list.addAll(mDownloadManager.getDownloading());
                        list.addAll(mDownloadManager.getPausedownload());
                        list.addAll(mDownloadManager.getReadyDownload());
                    }

                    @Override
                    public void getAllComplete(List<DownloadInfo> list) {
                        list.clear();
                        list.addAll(mDownloadManager.getCompleteDownload());
                    }

                    @Override
                    public boolean isNotWork() {
                        if (mDownloadManager.getDownloading().isEmpty())
                            return true;
                        return false;
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
