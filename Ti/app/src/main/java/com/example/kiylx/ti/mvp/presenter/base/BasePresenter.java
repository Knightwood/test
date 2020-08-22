package com.example.kiylx.ti.mvp.presenter.base;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

import com.example.kiylx.ti.mvp.contract.base.BaseContract;

/**
 * 创建者 kiylx
 * 创建时间 2020/6/11 0:15
 */
public abstract class BasePresenter<V extends BaseContract.View> implements BaseContract.Presenter<V> {
   protected V viewContract;

    public BasePresenter(V viewContract) {
        attachView(viewContract);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

    }

    @Override
    public void onRestoreInstanceState(Bundle outState) {

    }

    @Override
    public void attachView(@NonNull V view) {
        this.viewContract = view;
    }

    @Override
    public void detachView() {
        this.viewContract = null;
    }

    @Override
    public void onViewInitialized() {

    }

    @Nullable
    @Override
    public Context getContext() {
        return null;
    }
}
