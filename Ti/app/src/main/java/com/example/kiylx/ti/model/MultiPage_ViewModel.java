package com.example.kiylx.ti.model;

import android.util.Log;

/**
 * 用于多窗口更新文字，使用的databinding
 */
public class MultiPage_ViewModel {
    private static final String TAG="MultiPage_ViewModel";
    private String title;
    private String Url;
    private int pos;

    public MultiPage_ViewModel(){
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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
    public void ClickMethod(){
        Log.d(TAG, "ClickMethod: 选项被点击了");
    }
}
