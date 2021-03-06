package com.example.kiylx.ti.ui.activitys;

import androidx.appcompat.view.menu.MenuBuilder;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;

import com.example.kiylx.ti.R;
import com.example.kiylx.ti.model.WebPage_Info;
import com.example.kiylx.ti.adapters.historysadapter.HistoryListAdapter;
import com.example.kiylx.ti.viewmodel.HistorysViewModel;
import com.example.kiylx.ti.interfaces.OpenOneWebpage;
import com.example.kiylx.ti.mvp.contract.base.BaseLifecycleObserver;
import com.example.kiylx.ti.mvp.presenter.BookmarkManagerPresenter;
import com.example.kiylx.ti.tool.LogUtil;
import com.example.kiylx.ti.ui.base.BaseRecy_search_ViewActivity;

public class HistorysActivity extends BaseRecy_search_ViewActivity {
    private RecyclerView recyclerView;

    private HistoryListAdapter adapter;
    private HistorysViewModel model;

    private ClickMethod method;
    private static final String TAG = "历史activity";
    private static OpenOneWebpage sOpenOneWebpage;//打开网页的接口

    public static void setInterface(OpenOneWebpage openOneWebpage) {
        sOpenOneWebpage = openOneWebpage;
    }

    @Override
    protected void initActivity(BaseLifecycleObserver observer, Bundle savedInstanceState) {
        if (method == null) {
            method = new ClickMethod();
        }

        recyclerView = findViewById(R.id.base_search_recyclerView);
        adapter = new HistoryListAdapter(method, this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        model = new ViewModelProvider(this).get(HistorysViewModel.class);

        model.historiesLivePagedList.observe(this, historyEntities -> {
            try {
                Log.e(TAG, "Pageall: " + historyEntities.size());
            } catch (Exception e) {
                e.printStackTrace();
            }
            adapter.submitList(historyEntities);//给adapter数据
            LogUtil.d("重写后的历史记录activity", "onChanged: " + historyEntities);
        });
        model.query("");
        recyclerView.setAdapter(adapter);
        setToolbarTitle("历史记录", null);
    }

    /**
     * @param searchView 搜索历史记录
     */
    @Override
    protected void searchControl(SearchView searchView) {
        searchView.setOnCloseListener(() -> {
            //关闭搜索时显示全部item
            model.query("");
            return false;
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                model.query(query);
                return true;
            }

            @SuppressLint("CheckResult")
            @Override
            public boolean onQueryTextChange(String newText) {
                LogUtil.d(TAG, "onQueryTextChange: " + newText);
                return false;
            }
        });
    }

    @Override
    protected void addOtherMenu(Menu menu) {
        super.addOtherMenu(menu);
        menu.add(0,2,1,"清空");
    }

    @Override
    protected void ListenItemClick(MenuItem item) {
        super.ListenItemClick(item);
        switch (item.getItemId()) {
            case 2:

        break;
        }
    }

    private class ClickMethod implements HistoryListAdapter.AfterClick {

        @Override
        public void After(String url, boolean closeActivity, HistoryListAdapter.Action action) {
            if (sOpenOneWebpage==null){
                return;
            }
            switch (action) {
                case JUSTOPEN:
                    sOpenOneWebpage.loadUrl(url, false);
                    finish();
                    break;
                case OPENINNEWWINDOW:
                    sOpenOneWebpage.loadUrl(url, true);
                    finish();
                    break;
            }
        }

        @Override
        public void After(String url, String title, boolean b, HistoryListAdapter.Action action) {
            if (action == HistoryListAdapter.Action.ADDTOBOOKMARK) {
                addToBookMark(url, title);
            }
        }
    }

    public void addToBookMark(String url,String title) {
        //把当前网页信息传给收藏dialog
        Intent i = EditBookmarkActivity.newInstance(new WebPage_Info.Builder(url).title(title).build(), this, BookmarkManagerPresenter.DefaultBookmarkFolder.folderName);
        startActivity(i);
    }
}
