package com.crystal.customview.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.crystal.customview.R;
import com.crystal.customview.bottomview.MyBottomSheet;
import com.crystal.customview.bottomview.UseAppbarLayout;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    Button button_bottom_sheet;
    Button button_appbar;

    private static final String TAG = "mainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

      button_bottom_sheet=findViewById(R.id.bottom_sheet);
       button_appbar=findViewById(R.id.use_appbarlayout);

        testPrim();
        startSecondActivity();
        startBottomSheet();
        startAppbar();
    }

    private void startAppbar() {
        button_appbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StartActivity(UseAppbarLayout.class);
            }
        });
    }

    private void startBottomSheet() {
        button_bottom_sheet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StartActivity(MyBottomSheet.class);
            }
        });
    }

    public void startSecondActivity() {
        Button button=findViewById(R.id.button1);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StartActivity(SecondActivity.class);
            }
        });


    }

    public void testPrim() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                Toast.makeText(this, "当前无权限，请授权", Toast.LENGTH_SHORT);
                startActivityForResult(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName())), 0);
            }
        }
    }

    private void StartActivity(Class cls){
        Intent intent=new Intent(MainActivity.this,cls);
        startActivity(intent);
    }
}
