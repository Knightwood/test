package com.example.kiylx.ti.downloadpack.downloadcore;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.kiylx.ti.downloadpack.bean.DownloadInfo;
import com.example.kiylx.ti.downloadpack.dinterface.DownloadClickMethod;
import com.example.kiylx.ti.tool.LogUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 下载服务,作为downloadManager的代理，以及保持下载管理器不会被杀死
 */
public class DownloadServices extends Service {
    private DownloadBinder mDownloadBinder;
    private DownloadManager mDownloadManager;
    private DownloadClickMethod controlMethod;
    private List<DownloadInfo> downloadInfoList;
    private List<DownloadInfo> completeInfoList;

    @Override
    public void onCreate() {
        super.onCreate();
        mDownloadManager = DownloadManager.getInstance();
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

                    /**
                     * @return 获取下载条目list，若downloadInfoList不为null，那就置downloadInfoList为null，然后重新获取下载条目list
                     */
                    @Override
                    public List<DownloadInfo> getAllDownload() {
                        if (downloadInfoList == null) {
                            downloadInfoList = new ArrayList<>();

                            downloadInfoList.addAll(mDownloadManager.getDownloading());
                            downloadInfoList.addAll(mDownloadManager.getPausedownload());
                            downloadInfoList.addAll(mDownloadManager.getReadyDownload());
                        } else {
                            downloadInfoList = null;
                            getAllDownload();
                        }
                        LogUtil.d("下载服务：", "暂停下载：" + mDownloadManager.getPausedownload().size() + "\n"
                                + "正在下载：" + mDownloadManager.getDownloading().size() + "\n"
                                + "准备下载：" + mDownloadManager.getReadyDownload().size() + "\n"
                        );

                        return downloadInfoList;

                    }


                    @Override
                    public List<DownloadInfo> getAllComplete() {
                        if (completeInfoList == null) {
                            completeInfoList = new ArrayList<>();
                            completeInfoList.addAll(mDownloadManager.getCompleteDownload());
                        } else {
                            completeInfoList = null;
                            getAllComplete();
                        }
                        return completeInfoList;
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
