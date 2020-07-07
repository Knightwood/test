package com.example.kiylx.ti.tool;

import android.annotation.SuppressLint;
import android.app.usage.NetworkStatsManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.webkit.WebView;

import com.bumptech.glide.Glide;
import com.example.kiylx.ti.Xapplication;
import com.example.kiylx.ti.tool.dateProcess.TimeProcess;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * 创建者 kiylx
 * 创建时间 2020/3/29 17:54
 */
public enum SomeTools {
    INSTANCES;

    private WeakReference<Context> weakReference;

    public void SetContext(Context context){
        this.weakReference = new WeakReference<>(context);
    }

    @SuppressLint("CheckResult")
    public void savePic(String url) {
        Observable.create(new ObservableOnSubscribe<File>() {
            @Override
            public void subscribe(ObservableEmitter<File> emitter) throws Exception {
                emitter.onNext(Glide.with(weakReference.get().getApplicationContext())
                        .asFile()
                        .load(url)
                        .submit()
                        .get());
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.io())
                .subscribe(new Consumer<File>() {
                    @Override
                    public void accept(File file) throws Exception {
                        //获取到下载得到的图片，进行本地保存
                        File folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsoluteFile();
                        if (!folder.exists()) {
                            folder.mkdirs();
                        }
                        String fileName = System.currentTimeMillis() + ".jpg";

                        File destFile = new File(folder, fileName);
                        //把gilde下载得到图片复制到定义好的目录中去
                        copy(file, destFile);

                        // 最后通知图库更新
                        weakReference.get().getApplicationContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                                Uri.fromFile(new File(destFile.getPath()))));

                    }
                });
    }

    /**
     * 复制文件
     *
     * @param source 输入文件
     * @param target 输出文件
     */
    public void copy(File source, File target) {
        FileInputStream fileInputStream = null;
        FileOutputStream fileOutputStream = null;
        try {
            fileInputStream = new FileInputStream(source);
            fileOutputStream = new FileOutputStream(target);
            byte[] buffer = new byte[1024];
            while (fileInputStream.read(buffer) > 0) {
                fileOutputStream.write(buffer);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fileInputStream.close();
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * @param text text文本
     *             复制文本到剪切板
     */
    public void clipData(String text) {
        ClipboardManager manager = (ClipboardManager) weakReference.get().getApplicationContext().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("网址", text);
        manager.setPrimaryClip(clip);
    }

    /**
     * @param tmp 要打印的webview视图
     *            此方法使用的context只能是activity的context
     */
    public void printPdf(WebView tmp){
        PrintManager printManager = (PrintManager) weakReference.get().getSystemService(Context.PRINT_SERVICE);
        PrintDocumentAdapter adapter = tmp.createPrintDocumentAdapter(tmp.getTitle() + TimeProcess.getTime() + ".pdf");
        PrintAttributes attributes = new PrintAttributes.Builder()
                .setMediaSize(PrintAttributes.MediaSize.ISO_A4)
                .setResolution(new PrintAttributes.Resolution("id", Context.PRINT_SERVICE, 200, 200))
                .setColorMode(PrintAttributes.COLOR_MODE_COLOR)
                .setMinMargins(PrintAttributes.Margins.NO_MARGINS)
                .build();
        printManager.print(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath() + tmp.getTitle() + TimeProcess.getTime() + ".pdf", adapter, attributes);

    }

    /**
     * @return 获取Xapplicatin实例
     */
    public static Xapplication getXapplication(){
        return (Xapplication) Xapplication.getInstance();
    }

    /**
     * @param context
     * @return 返回网络状况，是使用的wifi还是流量又或者没有打开网络
     */
    public static NetState getNetState(Context context){
        ConnectivityManager manager= (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo=manager.getActiveNetworkInfo();//方法 getActiveNetworkInfo() 返回 NetworkInfo 实例，表示其可以找到的第一个已连接的网络接口，如果未连接任何接口，则返回 null（意味着互联网连接不可用）
        if (networkInfo!=null&&networkInfo.isConnected()){
            for (Network netWork : manager.getAllNetworks()) {
                NetworkInfo info=manager.getNetworkInfo(netWork);
               if (info.getType()==ConnectivityManager.TYPE_WIFI){
                   return NetState.WIFI;
               }
               if (info.getType()==ConnectivityManager.TYPE_MOBILE){
                   return NetState.DATA;
               }
               return NetState.OFF;
            }
        }
        return NetState.OFF;
    }

    public static boolean getNetWorkConnected(Context context){
        //方法 getActiveNetworkInfo() 返回 NetworkInfo 实例，表示其可以找到的第一个已连接的网络接口，如果未连接任何接口，则返回 null（意味着互联网连接不可用）
        ConnectivityManager manager= (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo=manager.getActiveNetworkInfo();
        if (networkInfo!=null&&networkInfo.isConnected())
            return true;
        return false;
    }

}
