package com.example.kiylx.ti.adapters.bookmarkadapter;

import android.view.View;

import com.example.kiylx.ti.model.WebPage_Info;

/**
 * 创建者 kiylx
 * 创建时间 2020/8/25 19:17
 * packageName：com.example.kiylx.ti.adapters
 * 描述：
 */
public interface TouchMethod {
    //点击和长按书签item时被回调的方法，在bookmarkmanageractivity中实现
    void click_bookmark(View view, WebPage_Info info);
    boolean onLongClick_bookmark(View view, WebPage_Info info);

    //点击和长按书签文件夹item时被回调的方法，在bookmarkmanageractivity中实现
    void click_folder(View view, WebPage_Info info);
    boolean onLongClick_folder(View view, WebPage_Info info);
}
