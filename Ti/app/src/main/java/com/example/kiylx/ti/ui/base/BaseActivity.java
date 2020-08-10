package com.example.kiylx.ti.ui.base;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.kiylx.ti.mvp.contract.base.BaseLifecycleObserver;
import com.example.kiylx.ti.mvp.contract.base.BaseContract;
import com.example.kiylx.ti.tool.networkpack.NetBroadcastReceiver;

import java.util.List;

/**
 * 创建者 kiylx
 * 创建时间 2020/6/12 17:28
 */
public abstract class BaseActivity extends AppCompatActivity implements BaseContract.View {
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

    @Override
    public void showProgressDialog(String content) {

    }

    @Override
    public void dismissProgressDialog() {

    }

    @Override
    public ProgressDialog getProgressDialog(String content) {
        return null;
    }

    @Override
    public void showTipDialog(String content) {

    }

    @Override
    public void showConfirmDialog(String msn, String title, String confirmText, DialogInterface.OnClickListener confirmListener) {

    }

    @Override
    public void showToast(String message) {

    }

    @Override
    public void showInfoToast(String message) {

    }

    @Override
    public void showSuccessToast(String message) {

    }

    @Override
    public void showErrorToast(String message) {

    }

    @Override
    public void showWarningToast(String message) {

    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void showLoginPage() {

    }

    @Override
    public <T> void updateUI(List<T> list) {

    }
}
