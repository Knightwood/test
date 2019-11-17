package com.example.kiylx.ti.DownloadCore;

import com.example.kiylx.ti.Corebase.DownloadInfo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import okhttp3.Response;

public class DownloadTaskRunnable implements Runnable {

    public static final int TYPE_SUCCESS = 0;
    public static final int TYPE_FAILED = 1;
    public static final int TYPE_PAUSED = 2;
    public static final int TYPE_CANCELED = 3;

    private int blockid;
    private DownloadInfo mDownloadInfo;
    private OkhttpManager mOkhttpManager;


    private RandomAccessFile rf;
    private File file;
    private Response response;
    private InputStream in = null;

    public DownloadTaskRunnable(DownloadInfo info, int blockid) {
        this.mDownloadInfo = info;
        this.blockid = blockid;
        mOkhttpManager = OkhttpManager.getInstance();

    }

    /**
     * @throws FileNotFoundException 做一些初始化工作，创建file，randomAccessfile，获取response
     */
    private void initFile() {
        try {
            response = mOkhttpManager.getResponse(mDownloadInfo, blockid);
            file = new File(mDownloadInfo.getPath() + mDownloadInfo.getFileName());
            rf = new RandomAccessFile(file, "rw");
            rf.seek(mDownloadInfo.splitStart[blockid]);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void closeThings() {
        try {
            if (response != null) {
                response.close();
            }
            if (rf != null) {
                rf.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            initFile();
            if (response != null) {
                this.in = response.body().byteStream();

                byte b[] = new byte[1024];

                //是记录现在开始下载的长度
                long lengthNow = mDownloadInfo.splitStart[blockid];
                //从流中读取的数据长度
                int len;
                while ((len = in.read(b)) != -1) {
                    if (mDownloadInfo.isPause()) {

                    } else if (mDownloadInfo.isCancel()) {

                    }
                    rf.write(b, 0, len);
                    //计算出总的下载长度
                    lengthNow += len;

                }

            }


        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

    }


}
