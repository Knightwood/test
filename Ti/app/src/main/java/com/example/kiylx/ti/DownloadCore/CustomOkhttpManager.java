package com.example.kiylx.ti.DownloadCore;

import com.example.kiylx.ti.Corebase.DownloadInfo;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CustomOkhttpManager {
    private OkHttpClient mOkHttpClient;
    private static CustomOkhttpManager mcustomHttpManager;

    private CustomOkhttpManager() {
        mOkHttpClient = new OkHttpClient();
    }

    public static CustomOkhttpManager getMcustomHttpManager() {
        if (mcustomHttpManager == null) {
            mcustomHttpManager = new CustomOkhttpManager();
        }
        return mcustomHttpManager;
    }

    public Call getCall(String url) {
        Request request = new Request.Builder()
                .url(url)
                .build();
        return mOkHttpClient.newCall(request);
    }

    public Response getResponse(DownloadInfo info) throws IOException {
        Request request = new Request.Builder()
                .url(info.getUrl())
                .addHeader("Range", "bytes=" + "-" + info.getRangeStart() + "-" + info.getRangeEnd())
                .build();
        return mOkHttpClient.newCall(request).execute();
    }


}
