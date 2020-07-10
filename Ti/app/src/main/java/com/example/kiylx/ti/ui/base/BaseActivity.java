package com.example.kiylx.ti.ui.base;

import android.content.IntentFilter;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.kiylx.ti.mvp.contract.BaseLifecycleObserver;
import com.example.kiylx.ti.tool.networkpack.NetBroadcastReceiver;

/**
 * 创建者 kiylx
 * 创建时间 2020/6/12 17:28
 */
public abstract class BaseActivity extends AppCompatActivity {
    public NetBroadcastReceiver receiver;
    private BaseLifecycleObserver lifecycleObserver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initActivity(lifecycleObserver);
    }

    protected abstract void initActivity(BaseLifecycleObserver observer);

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //unRegisterBroadCast();
    }

    /**
     * 注册广播，监听网络变化
     */
    public void registerBroadCast() {
        receiver = new NetBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(receiver, intentFilter);//注册广播
    }

    public void unRegisterBroadCast(){
        unregisterReceiver(receiver);
    }
}
