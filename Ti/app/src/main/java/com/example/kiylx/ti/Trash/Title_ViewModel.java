package com.example.kiylx.ti.trash;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

/**
 * @创建者 kiylx
 * @创建时间 2020/2/15 15:16
 *
 * mainactivity中底栏中的搜索框的文字
 * 设置搜索引擎里的文字
 */
public class Title_ViewModel extends BaseObservable {
    @Bindable
    public String title;


    public Title_ViewModel(String title){
        this.title=title;
    }

    public void setTitle(String title){
        this.title=title;
        notifyChange();
    }

}
