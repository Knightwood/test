package com.example.kiylx.sidebar;

import android.annotation.TargetApi;
import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

public class FloatingService extends Service {
    WindowManager windowManager;
    WindowManager.LayoutParams params;
        Button button;
    public FloatingService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        showFloatingWindow();
        return super.onStartCommand(intent,flags,startId);
    }



    private void showFloatingWindow(){
        if(Settings. canDrawOverlays(this)){
            windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
            button = new Button(getApplicationContext());
            button.setText("@string/Window");
            button.setBackgroundColor(Color.BLUE);
            button.setWidth(100);
            button.setHeight(100);
            button.setId(R.id.button_11);
            
            Log.d("mainActivity","on_2");


            params = new WindowManager.LayoutParams();
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                params.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
                Log.d("mainActivity","on_1");
            }else {
                params.type = WindowManager.LayoutParams.TYPE_PHONE;
            }
            params.gravity = Gravity.LEFT | Gravity.TOP;
            params.format = PixelFormat.RGBA_8888;
            params.width = 500;
            params.height = 100;
            params.x = 300;
            params.y = 300;
            windowManager.addView(button,params);
            button.setOnClickListener(new View.OnClickListener() {
                long[] hints = new long[2];
                @Override
                public void onClick(View view) {

                    System.arraycopy(hints,1,hints,0,hints.length -1);
                    hints[hints.length -1]=SystemClock.uptimeMillis();
                    if(SystemClock.uptimeMillis()-hints[0]>=2000){
                        Toast.makeText(FloatingService.this,"连续点击两次以退出", Toast.LENGTH_SHORT).show();
                    }else{
                        stopSelf();
                        Log.d("mainActivity","hb");
                    }


                }
            });
            Log.d("mainActivity","on");
        }
        }
    @Override
    public void onDestroy(){
        if(button !=null){
            windowManager.removeView(button);
        }
        super.onDestroy();
        Log.d("mainActivity","jj");

    }

    }

