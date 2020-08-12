package com.example.kiylx.ti.ui.activitys;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;


import com.example.kiylx.ti.R;
import com.example.kiylx.ti.model.WebPage_Info;
import com.example.kiylx.ti.mvp.contract.BookmarkActivityContract;
import com.example.kiylx.ti.mvp.contract.base.BaseLifecycleObserver;
import com.example.kiylx.ti.mvp.presenter.BookmarkManager;
import com.example.kiylx.ti.ui.base.BaseRecy_search_ViewActivity;
import com.example.kiylx.ti.xapplication.Xapplication;

import java.util.List;

public class BookmarkManagerActivity extends BaseRecy_search_ViewActivity implements BookmarkActivityContract {
    BookmarkManager bookmarkManager;

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
    }

    @Override
    protected void initActivity(BaseLifecycleObserver observer) {
        bookmarkManager = BookmarkManager.getInstance(Xapplication.getInstance(), this);
        setToolbarTitle("收藏夹", null);
    }

    /**
     * 更新界面
     *
     * @param folderAndBookmark 包含有文件夹list和书签list的键值对
     */
    @Override
    public void UpdateUI(List<WebPage_Info> folderAndBookmark) {
        showToast("接收到了更新");
    }

    @Override
    protected void searchControl(SearchView searchView) {

    }

    @Override
    protected void addOtherMenu(Menu menu) {
        super.addOtherMenu(menu);
        menu.add(0, 1, 1, R.string.addBookmarkFolder);
    }

    @Override
    protected void ListenItem(MenuItem item) {
        super.ListenItem(item);
    }
}
