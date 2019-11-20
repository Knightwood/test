package com.example.kiylx.ti.activitys;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;

import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;

import com.example.kiylx.ti.Discard.CustomDownloadService;
import com.example.kiylx.ti.R;

public class DownloadActivity extends AppCompatActivity {

    private CustomDownloadService.DownloadBinder downloadBinder;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            downloadBinder=(CustomDownloadService.DownloadBinder) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);

//绑定服务
        Intent intent = new Intent(DownloadActivity.this,CustomDownloadService.class);
        startService(intent);
        bindService(intent,connection,BIND_AUTO_CREATE);


    }



}
