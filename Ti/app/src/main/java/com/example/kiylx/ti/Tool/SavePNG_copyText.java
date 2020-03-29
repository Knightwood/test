package com.example.kiylx.ti.Tool;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.example.kiylx.ti.myInterface.ControlWebView;

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
public class SavePNG_copyText {
    private SavePNG_copyText copyText;
    private WeakReference<Context> weakReference;
    //private Context context;


    public SavePNG_copyText(Context context){
        //this.context=context;
        this.weakReference=new WeakReference<>(context);
    }

    public SavePNG_copyText getInstance(Context context){
        if (copyText==null){
            copyText=new SavePNG_copyText(context);
        }
        return copyText;
    }

    @SuppressLint("CheckResult")
    public void saveImage(String url) {
        Observable.create(new ObservableOnSubscribe<File>() {
            @Override
            public void subscribe(ObservableEmitter<File> e) throws Exception {
                //通过gilde下载得到file文件,这里需要注意android.permission.INTERNET权限
                e.onNext(Glide.with(weakReference.get().getApplicationContext())
                        .load(url)
                        .downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                        .get());
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(Schedulers.newThread())
                .subscribe(new Consumer<File>() {
                    @Override
                    public void accept(File file) throws Exception {
                        //获取到下载得到的图片，进行本地保存
                        File appDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsoluteFile();
                        if (!appDir.exists()) {
                            appDir.mkdirs();
                        }
                        String fileName = System.currentTimeMillis() + ".jpg";
                        File destFile = new File(appDir, fileName);
                        //把gilde下载得到图片复制到定义好的目录中去
                        copy(file, destFile);

                        // 最后通知图库更新
                        weakReference.get().getApplicationContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                                Uri.fromFile(new File(destFile.getPath()))));

                        /*runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(context, "保存成功", Toast.LENGTH_SHORT).show();
                            }
                        });*/

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
        ClipboardManager manager = (ClipboardManager)weakReference.get().getApplicationContext().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("网址", text);
        manager.setPrimaryClip(clip);


    }
}
