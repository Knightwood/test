package com.example.kiylx.ti.DownloadCore;

import com.example.kiylx.ti.Corebase.DownloadInfo;

import java.util.Deque;
import java.util.LinkedList;

public class DownloadManager {

    private volatile static DownloadManager mDownloadManager;

    private Deque<DownloadInfo> readyDownload;
    private Deque<DownloadInfo> downloading;
    private Deque<DownloadInfo> pausedownload;

    public static DownloadManager getInstance(){
        if (mDownloadManager==null){
            synchronized (DownloadManager.class){
                if (mDownloadManager==null){
                    mDownloadManager=new DownloadManager();
                }
            }
        }
        return mDownloadManager;
    }

    private DownloadManager(){
        downloading=new LinkedList<>();
        pausedownload=new LinkedList<>();
        readyDownload=new LinkedList<>();

    }

    public void startDownload(){

    }
    public void pauseDownload(){

    }
    public void cancelDownload(){

    }

}
