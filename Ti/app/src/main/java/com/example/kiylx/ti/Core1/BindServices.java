package com.example.kiylx.ti.Core1;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.example.kiylx.ti.DownloadCore.DownloadServices;
import com.example.kiylx.ti.activitys.MainActivity;

import static android.content.Context.BIND_AUTO_CREATE;

public class BindServices {
public BindServices(){

}

    private void bindService( Context context, Service service){
        Intent intent=new Intent(context, DownloadServices.class);
        .startService(intent);
        bindService(intent,connection,BIND_AUTO_CREATE);
    }
    private ServiceConnection connection=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mDownloadBinder= (DownloadServices.DownloadBinder)service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
}
