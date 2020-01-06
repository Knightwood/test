package com.example.kiylx.ti.notification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import java.util.Arrays;

public class CustomNotificationChannel {
    public final static String DOWNLOAD = "download";
    private final static String DEFAULT = "default";
    private final static String MEDIA = "media";

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
