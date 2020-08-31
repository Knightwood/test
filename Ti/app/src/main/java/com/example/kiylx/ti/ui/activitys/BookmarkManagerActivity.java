package com.example.kiylx.ti.ui.activitys;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.SearchView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.view.ActionMode;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kiylx.ti.R;
import com.example.kiylx.ti.adapters.bookmarkadapter.BookmarkListAdapter;
import com.example.kiylx.ti.adapters.bookmarkadapter.TouchMethod;
import com.example.kiylx.ti.conf.ActionCode;
import com.example.kiylx.ti.conf.ActivityCode;
import com.example.kiylx.ti.interfaces.OpenOneWebpage;
import com.example.kiylx.ti.model.WebPage_Info;
import com.example.kiylx.ti.mvp.contract.BookmarkActivityContract;
import com.example.kiylx.ti.mvp.contract.base.BaseLifecycleObserver;
import com.example.kiylx.ti.mvp.presenter.BookmarkManagerPresenter;
import com.example.kiylx.ti.tool.LogUtil;
import com.example.kiylx.ti.ui.base.BaseRecy_search_ViewActivity;
import com.example.kiylx.ti.ui.fragments.dialogfragment.Edit_dialog;

import java.lang.ref.WeakReference;

public class BookmarkManagerActivity extends BaseRecy_search_ViewActivity implements BookmarkActivityContract, TouchMethod {
    private static final String TAG = "BookmarkActivity2";
    private static OpenOneWebpage mopenWeb;//用于调用mainactivity中的接口
    private BookmarkManagerPresenter sBookmarkManagerPresenter;
    private BookmarkListAdapter bookmarkListAdapter;
    public MyHandler handler;
    LinearLayoutManager linearLayoutManager;
    private int whichMenu = 1;// 2加载第二个菜单，1加载第一个


    @Override
    protected void initActivity(BaseLifecycleObserver observer, Bundle savedInstanceState) {
        createHandler();
        LogUtil.d(TAG, "触发initActivity()方法");
        sBookmarkManagerPresenter = BookmarkManagerPresenter.getInstance(this, handler);
        if (savedInstanceState != null) {
            String path = savedInstanceState.getString("currentPath", BookmarkManagerPresenter.DefaultBookmarkFolder.uuid);
            sBookmarkManagerPresenter.setCurrentPath(path);
        }

        setToolbarTitle("收藏夹", null);
        initRecyclerView();
        LogUtil.d(TAG, "BookmarkManagerActivity initActivity()");

    }

    /**
     * 初始化recyclerview
     */
    private void initRecyclerView() {
        RecyclerView recyclerViewb = findViewById(R.id.base_search_recyclerView);
        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerViewb.setLayoutManager(linearLayoutManager);
        recyclerViewb.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        if (null == bookmarkListAdapter) {
            LogUtil.d(TAG, "BookmarkManagerActivity 适配器是null");
            bookmarkListAdapter = new BookmarkListAdapter(sBookmarkManagerPresenter.getBookmarkList());
            bookmarkListAdapter.setTouchMethod(this);
            recyclerViewb.setAdapter(bookmarkListAdapter);
        } else {
            LogUtil.d(TAG, "BookmarkManagerActivity 适配器不是null");
            recyclerViewb.setAdapter(bookmarkListAdapter);
            bookmarkListAdapter.notifyDataSetChanged();
        }
        LogUtil.d(TAG, "BookmarkManagerActivity initRecyclerview()");
        //获取数据
        sBookmarkManagerPresenter.getIndex(BookmarkManagerPresenter.DefaultBookmarkFolder.uuid, true);
    }

    public static void setInterface(OpenOneWebpage minterface) {
        BookmarkManagerActivity.mopenWeb = minterface;
    }


    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("currentPath", sBookmarkManagerPresenter.getCurrentPath());
    }

    /**
     * 更新界面
     * 包含有文件夹list和书签list的键值对
     */
    @Override
    public void updateUI() {
            /*List<WebPage_Info> newData = sBookmarkManager.getBookmarkList();
            List<WebPage_Info> oldData = adapter.getData();
            adapter.setData(newData);
            DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new BookmarkCallback(oldData, newData));
            diffResult.dispatchUpdatesTo(adapter);*/
        //不使用差异计算时的测试
        bookmarkListAdapter.notifyDataSetChanged();
        for (WebPage_Info info : sBookmarkManagerPresenter.getBookmarkList()) {
            LogUtil.d(TAG, "开始更新界面，接收到的数据： " + info.getUuid());
        }
    }

    /**
     * 刷新当前文件夹路径下的recyclerview
     */
    public void reflash() {
        sBookmarkManagerPresenter.getIndex(sBookmarkManagerPresenter.getCurrentPath(), true);
    }

    @Override
    protected int actionModeMenuId() {
        return R.menu.bookmarkactionmode;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

        switch (item.getItemId()) {
            case R.id.actionModeDelete:
                sBookmarkManagerPresenter.deleteBookmarks(bookmarkListAdapter.getBeSelectItems());
                break;
            case R.id.actionModeMove:
                selectFolder();
                break;
            case R.id.actionModeSelectAll:
                bookmarkListAdapter.selectAll(true);
                break;
            case R.id.reName:
                LogUtil.d(TAG,"重命名");
                Edit_dialog edit_dialog2 = Edit_dialog.getInstance(bookmarkListAdapter.getSelectOne(0).getTitle(), this, ActionCode.reName);
                FragmentManager fm2 = getSupportFragmentManager();
                edit_dialog2.show(fm2, "EDIT_DIALOG");
                break;
        }
        return true;
    }

    @Override
    protected void searchControl(SearchView searchView) {

    }


    /**
     * 创建handler处理消息，更新界面
     */
    public void createHandler() {
        handler = new MyHandler(this);
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
                LogUtil.d(TAG,"新建书签文件夹");
                Edit_dialog edit_dialog = Edit_dialog.getInstance(null, this, ActionCode.createFolderName);
                FragmentManager fm = getSupportFragmentManager();
                edit_dialog.show(fm, "EDIT_DIALOG");
                break;
        }
    }


    /**
     * 打开书签文件夹activity，选择文件夹
     */
    private void selectFolder() {
        Intent intent = new Intent(this, BookmarkFolderActivity.class);
        startActivityForResult(intent, ActivityCode.selectBookmarkFolder);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == ActivityCode.editBookmarkCode) {
                //编辑了书签，那有可能书签的所属文件夹就不一致了，更新界面
                reflash();
            }
            if (requestCode == ActivityCode.selectBookmarkFolder) {
                if (data != null) {
                    //批量更改层级，刷新界面
                    LogUtil.d(TAG, "改变文件夹层到文件夹uuid" + data.getStringExtra("FatherFolder"));
                    sBookmarkManagerPresenter.changeLevel(bookmarkListAdapter.getBeSelectItems(), data.getStringExtra("FatherFolder"));
                }
            }
        }
    }

    @Override
    public void click_bookmark(View view, WebPage_Info info) {
        //点击书签
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
        LogUtil.d(TAG, "ITEM被触发" + view.getId());
    }

    @Override
    public boolean onLongClick_bookmark(View view, WebPage_Info info) {
        //长按
        if (actionMode != null) {
            return false;
        }

        getActionMode();
        return true;
    }

    @Override
    public void click_folder(View view, WebPage_Info info) {
        //点击文件夹
        sBookmarkManagerPresenter.enterFolder(info.getUuid());

    }

    @Override
    public boolean onLongClick_folder(View view, WebPage_Info info) {
        //长按文件夹
        if (actionMode != null) {
            return false;
        }
        getActionMode();
        return true;
    }

    @Override
    public void setResult(int requestCode, String request, String result) {
        switch (requestCode) {
            case ActionCode.createFolderName:
                sBookmarkManagerPresenter.createFolder(result, sBookmarkManagerPresenter.getCurrentPath());
                break;
            case ActionCode.reName:
                sBookmarkManagerPresenter.changeFolderName(bookmarkListAdapter.getSelectOne(0).getUuid(), result);
                break;
        }

    }

    @Override
    public void updateMenu(int whichMenu) {
        //更改actionmode的menu。
        //要加载的menu与当前的menu不同，则加载，相同则不再处理
        if (this.whichMenu != whichMenu) {
            this.whichMenu = whichMenu;
            actionMode.invalidate();
        }
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        menu.clear();
        if (whichMenu == 1) {
            mode.getMenuInflater().inflate(actionModeMenuId(), menu);
            return true;
        } else {
            mode.getMenuInflater().inflate(R.menu.edit_name, menu);
            return true;
        }
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        super.onDestroyActionMode(mode);
        bookmarkListAdapter.destroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (!sBookmarkManagerPresenter.getBackStack()) {
                sBookmarkManagerPresenter.clickBack();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
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
        LogUtil.d(TAG, "POPMENU被触发");

    }

    @Override
    protected void onStop() {
        super.onStop();
        LogUtil.d(TAG, "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
        sBookmarkManagerPresenter.destroy();
        sBookmarkManagerPresenter = null;
    }

    /**
     * 更新界面
     */
    public static class MyHandler extends Handler {
        WeakReference weakReference;

        MyHandler(BookmarkManagerActivity activity) {
            weakReference = new WeakReference(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            BookmarkManagerActivity activity = (BookmarkManagerActivity) weakReference.get();
            if (activity != null) {
                switch (msg.what) {
                    case BookmarkManagerPresenter.HandlerMsg.updateUI_bookmark_folder:
                        activity.updateUI();
                        break;
                    case BookmarkManagerPresenter.HandlerMsg.updateUI_flash:
                        activity.reflash();
                        break;
                }
            }

        }
    }
}
