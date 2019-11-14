package com.example.kiylx.ti.DownloadCore;

import android.os.Environment;

import com.example.kiylx.ti.Corebase.DownloadInfo;

import java.io.IOException;
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
        long start = 0;
        long size = info.getFileLength() / corenum;

        //建立数组准备存储分块信息
        info.splitEnd = new long[++corenum];
        info.splitStart = new long[++corenum];

        for (int i = 0; i < corenum; i++) {
            info.splitStart[i] = start;
            info.splitEnd[i] = start + size;
            start += size;

        }
    }

    public void startDownload(DownloadInfo info) throws IOException {
        processInfo(info);
        Response response=mOkhttpManager.getResponse(info);

    }

    public void pauseDownload() {

    }

    public void cancelDownload() {

    }

}
