package com.example.kiylx.ti.mvp.presenter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import com.example.kiylx.ti.mvp.contract.base.BaseContract;
import com.example.kiylx.ti.mvp.presenter.base.BasePresenter;

/**
 * 创建者 kiylx
 * 创建时间 2020/8/22 10:40
 * packageName：com.example.kiylx.ti.mvp.presenter
 * 描述：
 */
public class BookmarkManagerLoader<T extends BasePresenter<BaseContract.View>> extends CursorLoader {
    public BookmarkManagerLoader(@NonNull Context context) {
        super(context);
    }

}
