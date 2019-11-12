package com.example.kiylx.ti.DownloadCore;

import java.util.LinkedList;

public class DownloadManager {

    private volatile static DownloadManager mDownloadManager;
    private LinkedList mLinkedList;

    public static DownloadManager getInstance(){
        if (mDownloadManager==null){
            synchronized (DownloadManager.class){
                if (mDownloadManager==null){
                    mDownloadManager=new DownloadManager();
                }
            }
        }
    }

    private DownloadManager(){
mLinkedList=new LinkedList();
    }

    public void startDownload(){

    }
    public void pauseDownload(){

    }
    public void cancelDownload(){

    }

}
