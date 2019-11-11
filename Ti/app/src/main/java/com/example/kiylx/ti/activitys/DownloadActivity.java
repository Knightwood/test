package com.example.kiylx.ti.activitys;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.kiylx.ti.DownloadCore.DownloadService;
import com.example.kiylx.ti.Notification.NotificationChannels;
import com.example.kiylx.ti.R;

public class DownloadActivity extends AppCompatActivity {
    private DownloadService.DownloadBinder downloadBinder;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            downloadBinder=(DownloadService.DownloadBinder) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        Button button=findViewById(R.id.button2333);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadBinder.setNotification1();
            }
        });

        Intent intent = new Intent(DownloadActivity.this,DownloadService.class);
        startService(intent);
        bindService(intent,connection,BIND_AUTO_CREATE);


    }



}
