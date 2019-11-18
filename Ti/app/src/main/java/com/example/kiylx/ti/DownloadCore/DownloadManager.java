package com.example.kiylx.ti.DownloadCore;

import android.os.Environment;

import com.example.kiylx.ti.Corebase.DownloadInfo;
import com.example.kiylx.ti.INTERFACE.DOWNLOAD_TASK_FUN;

import java.io.File;
import java.io.IOException;
import java.io.PipedReader;
import java.io.RandomAccessFile;
import java.util.Deque;
import java.util.LinkedList;

import okhttp3.Response;

public class DownloadManager {

    private volatile static DownloadManager mDownloadManager;
    private OkhttpManager mOkhttpManager;


    private Deque<DownloadInfo> readyDownload;
    private Deque<DownloadInfo> downloading;
    private Deque<DownloadInfo> pausedownload;

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

    private DownloadManager() {
        downloading = new LinkedList<>();
        pausedownload = new LinkedList<>();
        readyDownload = new LinkedList<>();
        mOkhttpManager = OkhttpManager.getInstance();

    }

    private DOWNLOAD_TASK_FUN mTASK_fun=new DOWNLOAD_TASK_FUN() {
        @Override
        public long updateProcess() {
            return 0;
        }

        @Override
        public boolean pausedDownload() {
            return false;
        }

        @Override
        public boolean canceledDownload() {
            return false;
        }

        @Override
        public boolean downloadSucess() {
            return false;
        }
    };

    /**
     * @param url      下载地址
     * @param filePath 文件路径 null则为默认路径
     * @param fileName 文件名称 null则为默认名称
     * @param coreNum  线程数量 默认为8
     * @return DownloadInfo
     */
    public DownloadInfo generateDownloadInfo(String url, String filePath, String fileName, int coreNum) {
        String name = fileName;
        String path = filePath;
        if (fileName == null) {
            name = url.substring(url.lastIndexOf("/"));
        }
        if (filePath == null) {
            //默认路径
            path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
        }
        return new DownloadInfo(url, path, name, coreNum);
    }

    /**
     * @param info downloadinfo
     * @return 加工信息，生成文件分块下载信息等。
     */
    private void processInfo(DownloadInfo info) throws IOException {
        int corenum = info.getThreadNum();
        info.setFileLength(mOkhttpManager.getFileLength(info.getUrl()));

        long blocksize = (info.getFileLength() / corenum) - 1;

        //建立数组准备存储分块信息
        info.splitEnd = new long[++corenum];
        info.splitStart = new long[++corenum];
//开始块和结束块。
        for (int i = 0; i < corenum; i++) {
            info.splitStart[i] = i * blocksize;
            info.splitEnd[i] = (i + 1) * blocksize - 1;

        }
    }

    /**
     * @param info 文件下载信息
     * @throws IOException
     */
    public void startDownload(DownloadInfo info) throws IOException {
        //处理信息
        processInfo(info);

        File file = new File(info.getPath() + info.getFileName());
        if (!file.exists()) {
            RandomAccessFile rf = new RandomAccessFile(file, "rw");
            rf.setLength(info.getFileLength());
            rf.close();
        }

        int i = info.getThreadNum();
        for (int j = 0; j < i; j++) {
            TaskPool.getInstance().getExecutorService().execute(new DownloadTaskRunnable(info, j,mTASK_fun));
        }


    }

    public void pauseDownload(DownloadInfo info) {
        info.setPause(true);
    }

    public void cancelDownload(DownloadInfo info) {
        info.setCancel(true);
    }

}
