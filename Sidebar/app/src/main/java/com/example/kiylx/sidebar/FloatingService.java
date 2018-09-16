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
import android.support.annotation.StringRes;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.util.zip.Inflater;

public class FloatingService extends Service {
    WindowManager windowManager;
    WindowManager.LayoutParams params;
        //Button button;
        LinearLayout app_list;
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
    /*public void start_Apps(){

    }*/



    private void showFloatingWindow(){
        if(Settings. canDrawOverlays(this)){
            windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
            LayoutInflater inflater1 = LayoutInflater.from(getApplicationContext());
            app_list = (LinearLayout) inflater1.inflate(R.layout.sidebar, null);
            /*button = new Button(getApplicationContext());
            button.setText("Window");
            button.setBackgroundColor(Color.WHITE);
            button.setWidth(100);
            button.setHeight(100);
            button.setId(R.id.button_11);*/
            
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
            params.width = 100;
            params.height= WindowManager.LayoutParams.MATCH_PARENT;
            params.x = 300;
            params.y = 300;
            windowManager.addView(app_list,params);
            app_list.setOnClickListener(new View.OnClickListener() {
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
        if(app_list !=null){
            windowManager.removeView(app_list);
        }
        super.onDestroy();
        Log.d("mainActivity","jj");

    }

    }

