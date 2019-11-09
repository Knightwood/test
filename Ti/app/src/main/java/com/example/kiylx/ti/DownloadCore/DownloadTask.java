package com.example.kiylx.ti.DownloadCore;

import android.os.AsyncTask;
import android.os.Environment;

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

    public DownloadTask(DownloadListener mlistener) {
        this.listener=mlistener;
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
                int total=0;
                int len;

                //从流中读入数据，存入数组b，如果读到最后，返回-1
                while((len=in.read(b))!=-1){
                    if (isCanceled){
                        return TYPE_CANCELED;
                    }else if (isPaused){
                        return TYPE_PAUSED;
                    }else{
                        //len每次都会是1024，total是总的长度，total用于计算下载进度
                        total+=len;
                        //savedFile是RandomAccessFile对象，从上面跳过的位置处开始，从数组b的0位置读取，把数据写入文件，
                        savedFile.write(b,0,len);

                        //计算进度
                        int progress = (int) ((total + downloadedLength) * 100 / contentLength);
                        //刷新进度显示
                        publishProgress(progress);
                    }
                }
                //流已经读尽了，关闭回应，返回成功
                response.close();
                return TYPE_SUCCESS;

            }

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try {
                if (in!=null){
                    in.close();
                }
                if (savedFile!=null){
                    savedFile.close();
                }
                if (isCanceled&&file!=null){
                    file.delete();
                }
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        return TYPE_FAILED;

    }

    private long getContentLength(String downloadUrl)throws IOException{
        OkHttpClient client =new OkHttpClient();
        Request request=new Request.Builder()
                .url(downloadUrl)
                .build();
        Response response = client.newCall(request).execute();
        if (response!=null&&response.isSuccessful()){
            long contentLength = response.body().contentLength();
            response.close();
            return contentLength;
        }
        return 0;

    }


    @Override
    protected void onPostExecute(Integer status) {
        switch (status) {
            case TYPE_SUCCESS:
                listener.onSuccess();
                break;
            case TYPE_FAILED:
                listener.onFailed();
                break;
            case TYPE_PAUSED:
                listener.onPaused();
                break;
            case TYPE_CANCELED:
                listener.onCanceled();
            default:
                break;
        }
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        int progrss =values[0];
        if (progrss>lastProgress){
            //调用服务里实现的接口刷新下载通知
            listener.onProgress(progrss);
            //刷新lastProgress的数值，一开始lastProgress只是0，每次刷新进度，它的值都会被修改
            lastProgress=progrss;
        }
    }

}
