package com.example.kiylx.sidebar;

import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class mainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);
    }
    public void startFloatingService(View view){
        if(!Settings.canDrawOverlays(this)){
            Toast.makeText(this,"请授予悬浮窗权限",Toast.LENGTH_SHORT).show();
            startActivityForResult(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:"+getPackageName())), 0);
            }else{
            startService(new Intent(mainActivity.this,FloatingService.class));
            Log.d("mainActivity","on_5");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
            if (!Settings.canDrawOverlays(this)) {
                Toast.makeText(this, "授权失败", Toast.LENGTH_SHORT).show();
                Log.d("mainActivity","on_3");
            } else {
                Toast.makeText(this, "授权成功", Toast.LENGTH_SHORT).show();
                startService(new Intent(mainActivity.this, FloatingService.class));
                Log.d("mainActivity","on_4");
            }
        }
    }
    public void start_selte(View view){
        Intent intent = new Intent(mainActivity.this,appInfoActivity.class);
        startActivity(intent);
    }
}
