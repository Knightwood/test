package com.example.kiylx.ti.ui.activitys;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.SearchView;


import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kiylx.ti.R;
import com.example.kiylx.ti.adapters.bookmarkadapter.BookmarkListAdapter;
import com.example.kiylx.ti.adapters.bookmarkadapter.TouchMethod;
import com.example.kiylx.ti.conf.ActivityCode;
import com.example.kiylx.ti.interfaces.OpenOneWebpage;
import com.example.kiylx.ti.model.WebPage_Info;
import com.example.kiylx.ti.mvp.contract.BookmarkActivityContract;
import com.example.kiylx.ti.mvp.contract.base.BaseLifecycleObserver;
import com.example.kiylx.ti.mvp.presenter.BookmarkManagerPresenter;
import com.example.kiylx.ti.tool.LogUtil;
import com.example.kiylx.ti.ui.base.BaseRecy_search_ViewActivity;
import com.example.kiylx.ti.xapplication.Xapplication;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class BookmarkManagerActivity extends BaseRecy_search_ViewActivity implements BookmarkActivityContract, TouchMethod {
    private static final String TAG = "BookmarkActivity2";
    private static OpenOneWebpage mopenWeb;//用于调用mainactivity中的接口
    private BookmarkManagerPresenter sBookmarkManagerPresenter;
    private BookmarkListAdapter adapter;
    private RecyclerView recyclerView;
    private static boolean containBookmarks = true;//true则显示书签和文件夹。false则是只显示文件夹，这时就是选择文件夹模式
    private Handler handler;


    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        recyclerView = findViewById(R.id.base_search_recyclerView);
    }

    @Override
    protected void initActivity(BaseLifecycleObserver observer, Bundle savedInstanceState) {
        createHandler();
        LogUtil.d(TAG, "触发initActivity()方法");
        sBookmarkManagerPresenter = BookmarkManagerPresenter.getInstance(Xapplication.getInstance(), this, handler);
        setToolbarTitle("收藏夹", null);
        if (getIntent() != null) {
            containBookmarks = getIntent().getBooleanExtra("isShowBookmarks", true);
            if (!containBookmarks) {
                addButtonView();
            }
        }
        initRecyclerView();
    }

    /**
     * 初始化recyclerview
     */
    private void initRecyclerView() {
        if (containBookmarks) {
            adapter = new BookmarkListAdapter(this, sBookmarkManagerPresenter.getBookmarkList());
        } else {
            adapter = new BookmarkListAdapter(this, sBookmarkManagerPresenter.getBookmarkFolderList());
        }
        adapter.setTouchMethod(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(adapter);
        //获取数据
        if (containBookmarks) {
            sBookmarkManagerPresenter.getIndex(BookmarkManagerPresenter.DefaultBookmarkFolder.uuid, true);
        } else {
            sBookmarkManagerPresenter.getFolderIndex(BookmarkManagerPresenter.DefaultBookmarkFolder.uuid, true);
        }
    }

    public static void setInterface(OpenOneWebpage minterface) {
        BookmarkManagerActivity.mopenWeb = minterface;
    }

    /**
     * 更新界面
     * 包含有文件夹list和书签list的键值对
     */
    @Override
    public void updateUI() {
        showToast("接收到了更新");
        if (adapter == null) {
            adapter = new BookmarkListAdapter(this, sBookmarkManagerPresenter.getBookmarkList());
            recyclerView.setAdapter(adapter);
        } else {
            /*List<WebPage_Info> newData = sBookmarkManager.getBookmarkList();
            List<WebPage_Info> oldData = adapter.getData();
            adapter.setData(newData);
            DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new BookmarkCallback(oldData, newData));
            diffResult.dispatchUpdatesTo(adapter);*/
            //不使用差异计算时的测试
            adapter.setData(sBookmarkManagerPresenter.getBookmarkList());
            adapter.notifyDataSetChanged();
        }
        LogUtil.d(TAG, "开始更新界面，接收到的数据： ");
    }

    @Override
    protected void searchControl(SearchView searchView) {

    }


    /**
     * 创建handler处理消息，更新界面
     */
    public void createHandler() {
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case BookmarkManagerPresenter.HandlerMsg.updateUI_bookmark_folder:
                    case BookmarkManagerPresenter.HandlerMsg.updateUI_folder:
                        updateUI();
                        break;
                }
            }
        };

    }

    @Override
    protected void addOtherMenu(Menu menu) {
        super.addOtherMenu(menu);
        menu.add(0, 1, 1, R.string.addBookmarkFolder);
    }

    @Override
    protected void ListenItemClick(MenuItem item) {
        super.ListenItemClick(item);
        switch (item.getItemId()) {
            case 1:
                sBookmarkManagerPresenter.createFolder("测试", BookmarkManagerPresenter.DefaultBookmarkFolder.uuid);
                break;
        }
    }

    /**
     * 添加一个按钮
     */
    private void addButtonView() {
        MaterialButton button = new MaterialButton(this);
        button.setText(R.string.complete);

        ConstraintLayout.LayoutParams lp = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.bottomToBottom = R.id.base_search_recyclerView;
        lp.rightToRight = R.id.base_rec_root;
        lp.setMargins(10, 10, 30, 35);
        button.setLayoutParams(lp);

        addSomeView(button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getIntent();
                intent.putExtra("FolderName", sBookmarkManagerPresenter.queryFolderName(sBookmarkManagerPresenter.getUpperLevel()));
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == ActivityCode.editBookmarkCode) {
                //updateUI();
            }
        }
    }

    @Override
    public void click_bookmark(View view, WebPage_Info info) {
        switch (view.getId()) {
            case R.id.itemTitle:
                //点击item后访问网址
                finish();
                mopenWeb.loadUrl(info.getUrl(), true);
                break;
            case R.id.more_setting:
                addPopMenu(view, info);
                break;
        }
        LogUtil.d(TAG, "ITEM被触发"+view.getId());
    }

    @Override
    public void onLongClick_bookmark(View view, WebPage_Info info) {

    }

    @Override
    public void click_folder(View view, WebPage_Info info) {

    }

    @Override
    public void onLongClick_folder(View view, WebPage_Info info) {

    }

    /**
     * @param v    要添加popmenu的视图
     * @param info info
     */
    private void addPopMenu(View v, WebPage_Info info) {
        PopupMenu popupMenu = new PopupMenu(BookmarkManagerActivity.this, v);
        MenuInflater menuInflater = popupMenu.getMenuInflater();
        menuInflater.inflate(R.menu.bookmark_item_options, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.edit_Bookmark:
                        Intent i = EditBookmarkActivity.newInstance(info, getApplicationContext(), sBookmarkManagerPresenter.queryFolderName(info.getBookmarkFolderUUID()));
                        startActivityForResult(i, ActivityCode.editBookmarkCode);
                        break;
                    case R.id.delete_Bookmark:
                        sBookmarkManagerPresenter.deleteBookmark(info.getUuid());
                        break;
                    case R.id.openPageinNewWindow:
                        finish();
                        //因为mainactivity里加载网页代码太烂，这里写true用新标签页打开会有bug ？？？我当时写的什么意思
                        mopenWeb.loadUrl(info.getUrl(), true);
                }
                return false;
            }
        });
        popupMenu.show();
        LogUtil.d(TAG,"POPMENU被触发");

    }
}
