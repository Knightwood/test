package com.example.kiylx.ti.model;

import android.util.Log;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.example.kiylx.ti.BR;

/**
 * 用于多窗口更新文字，使用的databinding
 */
public class MultiPage_Bean extends BaseObservable {
    private static final String TAG = "MultiPage_Bean";
    private String title;
    private String Url;
    private int pos;

    public MultiPage_Bean() {
    }

    @Bindable
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        notifyPropertyChanged(com.example.kiylx.ti.BR.title);

    }

    public String getUrl() {
        return Url;
    }

    public void setUrl(String url) {
        Url = url;
    }

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    public void ClickMethod() {
        Log.d(TAG, "ClickMethod: 选项被点击了");
    }
}
