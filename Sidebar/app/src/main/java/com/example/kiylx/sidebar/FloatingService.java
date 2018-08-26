package com.example.kiylx.sidebar;

import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.view.WindowManager;
import android.widget.Button;

public class FloatingService extends Service {
    public FloatingService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        return super.onStartCommand(intent,flags,startId);
    }
    private void showFloatingWindow(){
        if(Settings.canDrawOverlays(this)){
            WindowManager windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
            Button button = new Button(getApplicationContext());
            button.setText("Floating Window");
            button.setBackgroundColor(Color.BLUE);

            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            if(Build.VERSION.SDK_INI >= Build.VERSION_CODES.0){
                layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
            }else {
                layoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
            }
            layoutParams.format = PixelFormat.RGBA_8888;
            layoutParams.width = 500;
            layoutParams.height = 100;
            layoutParams.x = 300;
            layoutParams.y = 300;
            windowManager.addView(button, layoutParams);
        }
        }

    }

