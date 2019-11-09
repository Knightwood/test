package com.example.kiylx.ti.Activitys;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;

import com.example.kiylx.ti.DownloadCore.DownloadService;
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

        Intent intent = new Intent(DownloadActivity.this,DownloadService.class);
        startService(intent);
        bindService(intent,connection,BIND_AUTO_CREATE);
    }
}
