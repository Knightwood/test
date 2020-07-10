package com.example.kiylx.ti.tool;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.webkit.WebView;

import com.bumptech.glide.Glide;
import com.example.kiylx.ti.Xapplication;
import com.example.kiylx.ti.tool.dateProcess.TimeProcess;
import com.example.kiylx.ti.tool.networkpack.NetState;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

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


    public void SetContext(Context context) {
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
    public void printPdf(WebView tmp) {
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
    public static Xapplication getXapplication() {
        return (Xapplication) Xapplication.getInstance();
    }

    /**
     * 判断是否有网络连接
     *
     * @param context
     * @return
     */
    public static boolean isNetworkConnected(@NotNull Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        if (networkInfo != null) {
            return networkInfo.isConnected();
        }
        return false;
    }

    /**
     * @param context context
     * @return 返回网络状态，wifi标识连接到了可用wifi，data标识连接到了数据网络，off标识没有网络
     */
    public static NetState getCurrentNetwork(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= 23) {
            NetworkCapabilities networkCapabilities = manager.getNetworkCapabilities(manager.getActiveNetwork());
            /*
             * hasCapability：
             * NET_CAPABILITY_INTERNET：表示是否连接到互联网，即是否连接上了WIFI或者移动蜂窝网络，这个为TRUE不一定能正常上网
             * NET_CAPABILITY_VALIDATED：表示是否确实能和连接的互联网通信，这个为TRUE，才是真的能上网
             * 这些参数表示的状态是可以共存的，也就是说networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)和networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)的返回值可能同时为TRUE。
             *
             * hasTransport：
             * TRANSPORT_CELLULAR：表示当前接入的是否是蜂窝网络
             * TRANSPORT_WIFI：表明当前接入的是WIFI网络，还有一些别的蓝牙网络，有线网络等等可以直接查看文档或源码了解
             * 这些参数表示的状态是不可共存的，即你不可能又连接到蜂窝网络又连接到WIFI网络，它们同时只会有一个返回TRUE。
             */
            if (networkCapabilities != null && networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)) {
                if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    return NetState.WIFI;
                }
                if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    return NetState.DATA;
                }
            } else {
                return NetState.OFF;
            }
        } else {
            NetworkInfo info = manager.getActiveNetworkInfo();
            if (info != null && info.isConnected()) {
                for (Network network : manager.getAllNetworks()) {
                    NetworkInfo info1 = manager.getNetworkInfo(network);
                    if (info.getType() == ConnectivityManager.TYPE_WIFI) {
                        return info1.isConnected() ? NetState.WIFI : NetState.OFF;
                    }
                    if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
                        return info1.isConnected() ? NetState.DATA : NetState.OFF;
                    }
                }
            }
        }
        return NetState.OFF;
    }

    /**
     * @param context
     * @return 返回网络状况，是使用的wifi还是流量又或者没有打开网络
     */
    @Deprecated
    public static Map<NetState, Boolean> getNetState(Context context) {
        Map<NetState, Boolean> result = null;
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();//方法 getActiveNetworkInfo() 返回 NetworkInfo 实例，表示其可以找到的第一个已连接的网络接口，如果未连接任何接口，则返回 null（意味着互联网连接不可用）
        if (networkInfo != null && networkInfo.isConnected()) {
            result = new HashMap<>();
            for (Network netWork : manager.getAllNetworks()) {
                NetworkInfo info = manager.getNetworkInfo(netWork);
                if (info.getType() == ConnectivityManager.TYPE_WIFI) {
                    result.put(NetState.WIFI, info.isConnected());
                }
                if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
                    result.put(NetState.DATA, info.isConnected());
                }
            }
        }
        return result;
    }

    public static boolean getNetWorkConnected(Context context) {
        //方法 getActiveNetworkInfo() 返回 NetworkInfo 实例，表示其可以找到的第一个已连接的网络接口，如果未连接任何接口，则返回 null（意味着互联网连接不可用）
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        return false;
    }

}
