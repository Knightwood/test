package com.example.kiylx.ti.downloadpack.downloadcore;

import com.example.kiylx.ti.downloadpack.base.DownloadInfo;
import com.example.kiylx.ti.downloadpack.dinterface.DOWNLOAD_TASK_FUN;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.Objects;

import okhttp3.Response;

public class DownloadTaskRunnable implements Runnable {


    private static final String TAG = "下载信息";

    private int blockid;
    private DownloadInfo mDownloadInfo;
    //private OkhttpManager mOkhttpManager;
    private DOWNLOAD_TASK_FUN mTASK_fun;


    private RandomAccessFile rf;
    private File file;
    private Response response;
    private InputStream in = null;

    public DownloadTaskRunnable(DownloadInfo info, int blockid, DOWNLOAD_TASK_FUN Interface) {
        this.mDownloadInfo = info;
        this.blockid = blockid;
        //mOkhttpManager = OkhttpManager.newInstance();
        mTASK_fun = Interface;

    }

    /**
     * @throws IOException 做一些初始化工作，创建file，randomAccessfile，获取response
     */
    private void initFile() {
        try {
            response = OkhttpManager.getInstance().getResponse(mDownloadInfo, blockid);
            System.out.println("fileName=" + mDownloadInfo.getFileName() + " 每个线程负责下载文件大小contentLength=" + response.body().contentLength()
                    + " 开始位置start=" + mDownloadInfo.splitStart[blockid] + "结束位置end=" + mDownloadInfo.splitEnd[blockid] + " threadId=" + blockid);

            file = new File(mDownloadInfo.getPath() + mDownloadInfo.getFileName());
            rf = new RandomAccessFile(file, "rw");
            rf.seek(mDownloadInfo.splitStart[blockid]);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 线程之行结束的清理
     */
    private void closeThings() {
        try {
            if (response != null) {
                response.close();
            }
            if (rf != null) {
                rf.close();
            }
            if (in != null) {
                in.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {

            initFile();//初始化处理

            if (response != null) {
                this.in = Objects.requireNonNull(response.body()).byteStream();

                byte[] b = new byte[1024];

                //从流中读取的数据长度
                int len;
                //流没有读尽和没有暂停时执行循环以写入文件
                while (((len = in.read(b)) != -1)&&!mDownloadInfo.isPause()) {

                        //+这里应该再添加一些resumeDownload的处理
                        rf.write(b, 0, len);
                        //计算出总的下载长度
                        mDownloadInfo.splitStart[blockid] += len;

                }
                //流没有读尽且暂停时的处理
                if ((mDownloadInfo.isPause())&&len!=-1) {
                    mTASK_fun.downloadPaused(mDownloadInfo);
                }else{
                    //分块下载成功
                    mTASK_fun.downloadSucess(mDownloadInfo);
                }

            }


        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        } finally {
            closeThings();
        }

    }
}
