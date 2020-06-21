package com.example.kiylx.ti.mvp.presenter;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

/**
 * 创建者 kiylx
 * 创建时间 2020/6/12 17:54
 */
public class MainLifeCycleObserver implements LifecycleObserver {

    public MainLifeCycleObserver() {
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    public void onCreateActivity() {

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onActivityStart() {

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void onActivityResume() {

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void onActivityPause() {

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onActivityStop() {

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void onActivityDestroy() {

    }
}
