package com.example.kiylx.ti.mvp.presenter;

import com.example.kiylx.ti.mvp.contract.MainActivityContract;
import com.example.kiylx.ti.mvp.presenter.base.BasePresenter;

/**
 * 创建者 kiylx
 * 创建时间 2020/8/23 8:38
 * packageName：com.example.kiylx.ti.mvp.presenter
 * 描述：
 */
public class MainActivityPresenter extends BasePresenter<MainActivityContract> {
    public MainActivityPresenter(MainActivityContract viewContract) {
        super(viewContract);
    }
}
