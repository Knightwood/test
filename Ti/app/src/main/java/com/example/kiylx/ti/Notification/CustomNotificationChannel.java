package com.example.kiylx.ti.Notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import java.util.Arrays;

public class CustomNotificationChannel {
    public final static String DOWNLOAD = "download";
    public final static String DEFAULT = "default";
    public final static String MEDIA = "media";

    public static void getChannel(NotificationManager nm){
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            nm.createNotificationChannels(Arrays.asList(
                    new NotificationChannel(DOWNLOAD,"下载通知",NotificationManager.IMPORTANCE_DEFAULT),
                    new NotificationChannel(DEFAULT,"通常",NotificationManager.IMPORTANCE_LOW),
                    new NotificationChannel(MEDIA,"媒体通知",NotificationManager.IMPORTANCE_HIGH)
            ));
        }

    }

}
