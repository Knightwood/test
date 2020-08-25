package com.example.kiylx.ti.adapters.bookmarkadapter;

import android.content.Intent;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import com.crystal.customview.baseadapter1.BaseAdapter;
import com.crystal.customview.baseadapter1.BaseHolder;
import com.example.kiylx.ti.R;
import com.example.kiylx.ti.conf.ActivityCode;
import com.example.kiylx.ti.model.WebPage_Info;
import com.example.kiylx.ti.tool.LogUtil;
import com.example.kiylx.ti.ui.activitys.BookmarkManagerActivity;
import com.example.kiylx.ti.ui.activitys.EditBookmarkActivity;

import java.util.List;

/**
 * 创建者 kiylx
 * 创建时间 2020/8/25 7:23
 * packageName：com.example.kiylx.ti.adapters.bookmarkadapter
 * 描述：
 */
public class BookmarkListAdapter extends BaseAdapter<WebPage_Info, BaseHolder<WebPage_Info>> {
    private static final String TAG = "BookmarkActivity2";
    private View.OnClickListener listener;
    private TouchMethod touchMethod;

    private BookmarkManagerActivity bookmarkManagerActivity;

    public BookmarkListAdapter(BookmarkManagerActivity bookmarkManagerActivity, List<WebPage_Info> list) {
        super(list);
        this.bookmarkManagerActivity = bookmarkManagerActivity;
    }

    @Override
    protected int getItemViewResId(int viewType) {
        //两种itemview直接使用一种布局就可以
        return R.layout.history_item;
    }

    @Override
    public int getViewType(WebPage_Info bean) {
        return bean.getWEB_feature() == -2 ? BookmarkKind.folder : BookmarkKind.bookmark;
    }

    @Override
    public BaseHolder<WebPage_Info> createHolder(View itemView, int viewType) {
        switch (viewType) {
            case BookmarkKind.folder:
                return new bookmarkFolderHolder(itemView);
            case BookmarkKind.bookmark:
                return new bookmarkholder(itemView);
        }
        return new BaseHolder<>(itemView);
    }

    @Override
    public void bind(BaseHolder<WebPage_Info> holder, WebPage_Info data, int viewType) {
        switch (viewType) {
            case BookmarkKind.folder:
                ((bookmarkFolderHolder) holder).bind(data);
                holder.setOnIntemClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        touchMethod.click_folder(v, data);
                        LogUtil.d(TAG,"点击书签文件夹被触发");
                    }
                });
                break;
            case BookmarkKind.bookmark:
                ((bookmarkholder) holder).bind(data);
                holder.setOnIntemClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        touchMethod.click_bookmark(v, data);
                        LogUtil.d(TAG,"点击书签被触发"+v.getId());
                    }
                });
                break;
        }

    }

    public void setTouchMethod(TouchMethod touchMethod) {
        this.touchMethod = touchMethod;
    }

    static class BookmarkKind {
        //item的类型
        public static final int folder = 1;
        public static final int bookmark = 2;
    }
}
