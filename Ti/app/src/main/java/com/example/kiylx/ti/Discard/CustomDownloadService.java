package com.example.kiylx.ti.Discard;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.example.kiylx.ti.DownloadCore.DownloadMethodListener;
import com.example.kiylx.ti.Notification.CustomNotificationChannel;
import com.example.kiylx.ti.activitys.BookmarkPageActivity;
import com.example.kiylx.ti.activitys.MainActivity;
import com.example.kiylx.ti.R;

import java.io.File;

public class CustomDownloadService extends Service {
    DownloadTask mDownloadTask;
    String downloadUrl;
    //获取通知管理器
    NotificationManager nm;

    public CustomDownloadService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //创建通知管理器以及设置通知渠道
        nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        CustomNotificationChannel.getChannel(nm);
    }

    private DownloadMethodListener mDownloadMethodListener = new DownloadMethodListener() {
        @Override
        public void onProgress(int progress) {
            getNotificationManager().notify(1, getNotification("Downloading...", progress));
        }

        @Override
        public void onSuccess() {
            mDownloadTask = null;
            // 下载成功时将前台服务通知关闭，并创建一个下载成功的通知
            stopForeground(true);
            getNotificationManager().notify(1, getNotification("下载成功", -1));
            Toast.makeText(CustomDownloadService.this, "Download Success", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onFailed() {
            mDownloadTask = null;
            // 下载失败时将前台服务通知关闭，并创建一个下载失败的通知
            stopForeground(true);
            getNotificationManager().notify(1, getNotification("下载失败", -1));
            Toast.makeText(CustomDownloadService.this, "Download Failed", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onPaused() {
            mDownloadTask = null;
            Toast.makeText(CustomDownloadService.this, "暂停中", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCanceled() {
            mDownloadTask = null;
            stopForeground(true);
            Toast.makeText(CustomDownloadService.this, "已取消", Toast.LENGTH_SHORT).show();
        }
    };

    private DownloadBinder mBinder = new DownloadBinder();

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.

        return mBinder;
    }

    public class DownloadBinder extends Binder {

        public void getNotification111() {
            //把通知加入通知管理器，发出通知
            nm.notify(1, sNotification());
        }

        public Notification sNotification() {
//这个intent用于点击通知的时候启动某个activity
            Intent intent1 = new Intent(CustomDownloadService.this, BookmarkPageActivity.class);
            PendingIntent pi = PendingIntent.getActivity(CustomDownloadService.this, 0, intent1, 0);

            //构建某个渠道的通知实例
            NotificationCompat.Builder builder = new NotificationCompat.Builder(CustomDownloadService.this, CustomNotificationChannel.DOWNLOAD);
            builder.setSmallIcon(R.mipmap.ic_launcher);
            builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
            builder.setContentIntent(pi);
            builder.setContentTitle("title");

            return builder.build();

        }

        public CustomDownloadService getService() {
            return CustomDownloadService.this;
        }

        public void startDownload(String url) {
            if (mDownloadTask == null) {
                downloadUrl = url;
                mDownloadTask = new DownloadTask(mDownloadMethodListener);
                mDownloadTask.execute(downloadUrl);

                startForeground(1, getNotification("下载中...", 0));
                Toast.makeText(CustomDownloadService.this, "Downloading...", Toast.LENGTH_SHORT).show();
            }
        }

        public void pauseDownload() {
            if (mDownloadTask != null) {
                mDownloadTask.pauseDownload();
            }
        }

        public void cancelDownload() {
            if (mDownloadTask != null) {
                mDownloadTask.canaelDownload();
            } else {
                if (downloadUrl != null) {
                    // 取消下载时需将文件删除，并将通知关闭
                    String fileName = downloadUrl.substring(downloadUrl.lastIndexOf("/"));
                    String directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
                    File file = new File(directory + fileName);
                    if (file.exists()) {
                        file.delete();
                    }

                    //通知
                    getNotificationManager().cancel(1);
                    stopForeground(true);
                    Toast.makeText(CustomDownloadService.this, "Canceled", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private NotificationManager getNotificationManager() throws NullPointerException{
        return this.nm;
    }

    private Notification getNotification(String title, int progress) {
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this, 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CustomNotificationChannel.DOWNLOAD);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
        builder.setContentIntent(pi);
        builder.setContentTitle(title);

        if (progress >= 0) {
            builder.setContentText(progress + "%");
            builder.setProgress(100, progress, false);
        }
        return builder.build();
    }
}
