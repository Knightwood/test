package com.example.kiylx.ti.ui.base;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * 创建者 kiylx
 * 创建时间 2020/6/12 17:28
 */
public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initActivity();
    }

    protected abstract void initActivity();
}
