package com.example.kiylx.ti.activitys;

import android.content.Context;
import android.os.Bundle;

import com.example.kiylx.ti.conf.PConf;
import com.example.kiylx.ti.conf.SomeRes;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;

import com.example.kiylx.ti.R;

public class StartPageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_page);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        initConf(this);
        getAuthority(this);
    }

    /**
     * @param startPageActivity
     * 申请权限
     */
    private void getAuthority(StartPageActivity startPageActivity) {
    }

    /**
     * @param context
     * 初始化设置文件
     */
    private void initConf(Context context) {
        //自定义主页
        PConf.putBoolean(this, SomeRes.homepage,false);
        //
    }

}
