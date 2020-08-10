package com.example.kiylx.ti.mvp.contract.base;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

/**
 * 创建者 kiylx
 * 创建时间 2020/7/6 10:21
 * 基础的lifecycleObserver的接口
 */
public interface BaseLifecycleObserver extends LifecycleObserver {
    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    void onCreateActivity();

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
   void onActivityStart();

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    void onActivityResume();

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
     void onActivityPause();

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    void onActivityStop();

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
     void onActivityDestroy();
}
