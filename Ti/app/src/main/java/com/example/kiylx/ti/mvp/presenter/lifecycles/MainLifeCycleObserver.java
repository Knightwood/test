package com.example.kiylx.ti.mvp.presenter.lifecycles;

import android.content.Context;

import com.example.kiylx.ti.mvp.contract.base.BaseLifecycleObserver;

/**
 * 创建者 kiylx
 * 创建时间 2020/6/12 17:54
 */
public class MainLifeCycleObserver implements BaseLifecycleObserver {
    Context mainActivity;

    public MainLifeCycleObserver(Context mainActivity) {
    mainActivity=mainActivity;
    }
    @Override
    public void onCreateActivity() {

    }

    @Override
    public void onActivityStart() {

    }

    @Override
    public void onActivityResume() {

    }

    @Override
    public void onActivityPause() {

    }

    @Override
    public void onActivityStop() {

    }

    @Override
    public void onActivityDestroy() {

    }
}
