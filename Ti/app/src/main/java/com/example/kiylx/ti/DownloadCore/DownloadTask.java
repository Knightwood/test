package com.example.kiylx.ti.DownloadCore;

import android.os.AsyncTask;
import android.os.Environment;
import android.webkit.DownloadListener;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DownloadTask extends AsyncTask<String, Integer, Integer> {

    public static final int TYPE_SUCCESS = 0;
    public static final int TYPE_FAILED = 1;
    public static final int TYPE_PAUSED = 2;
    public static final int TYPE_CANCELED = 3;

    private DownloadListener listener;
    private boolean isCanceled = false;
    private boolean isPaused = false;
    private int lastProgress;

    public DownloadTask(DownloadListener listener) {
        this.listener=listener;
    }

    @Override
    protected Integer doInBackground(String... strings) {
        InputStream in=null;
        RandomAccessFile savedFile=null;
        File file=null;
        try {
            long downloadedLength=0;
            String downloadUrl=strings[0];
            String fileName = downloadUrl.substring(downloadUrl.lastIndexOf("/"));
            String directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();//存储下载的路径

            file=new File(directory+fileName);

            //如果文件存在，说明已经下载或是刚开始，所以获取文件长度作为已下载的文件长度
            if (file.exists()){
                downloadedLength=file.length();
            }

            //要下载的文件的长度
            long contentLength=getContentLength(downloadUrl);

            //判断文件是否下载成功
            if (contentLength==0){
                return TYPE_FAILED;
            }else if (contentLength==downloadedLength){
                return TYPE_SUCCESS;
            }

            //利用添加Range:bytes=512000- 请求头实现断点传续
            OkHttpClient client =new OkHttpClient();
            Request request = new Request.Builder()
                    .url(strings[0])
                    .addHeader("RANGR","bytes="+downloadedLength+"-")
                    .build();
            //使用同步请求取得回应
            Response response=client.newCall(request).execute();

            //拿到服务器传进来的数据，存入新建的文件
            if (response!=null){
                //输入流
                in=response.body().byteStream();

                //创建一个随机输入文件，可以写入或读取文件。
                savedFile=new RandomAccessFile(file,"rw");
                savedFile.seek(downloadedLength);// 跳过已下载的字节

                byte[] b =new byte[1024];


            }

        }catch (Exception e){
            e.printStackTrace();
        }





        return TYPE_FAILED;

    }

    private long getContentLength(String downloadUrl) {
    }


    @Override
    protected void onPostExecute(Integer integer) {
        super.onPostExecute(integer);
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }

}
