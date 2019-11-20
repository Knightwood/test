package com.example.kiylx.ti.DownloadCore;

import com.example.kiylx.ti.Corebase.DownloadInfo;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class OkhttpManager {
    private OkHttpClient mOkHttpClient;
    private static OkhttpManager mHttpManager;

    private OkhttpManager() {
        mOkHttpClient = new OkHttpClient();
    }

    public static OkhttpManager getInstance() {
        if (mHttpManager == null) {
            mHttpManager = new OkhttpManager();
        }
        return mHttpManager;
    }

    public Call getCall(String url) {
        Request request = new Request.Builder()
                .url(url)
                .build();
        return mOkHttpClient.newCall(request);
    }

    /**
     * @param info 下载信息
     * @param blockid 块标号
     * @return 返回response
     * @throws IOException
     */
    public Response getResponse(DownloadInfo info, int blockid) throws IOException {
        Request request = new Request.Builder()
                .url(info.getUrl())
                .addHeader("Range", "bytes=" + info.splitStart[blockid] + "-" + info.splitEnd[blockid])
                .build();
        return mOkHttpClient.newCall(request).execute();
    }

    /**
     * @param url 下载地址
     * @return 返回下载文件大小
     */
    public long getFileLength(String url) throws IOException{


            Request request = new Request.Builder()
                    .url(url)
                    .build();
            Response response=mOkHttpClient.newCall(request).execute();
            if (response!=null&&response.isSuccessful()){
                long length=response.body().contentLength();
                response.close();
                return length;
            }
            else {
                return -1;
            }
    }


}
