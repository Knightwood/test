package com.example.kiylx.ti.ui.activitys;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kiylx.ti.R;
import com.example.kiylx.ti.adapters.bookmarkadapter.BookmarkListAdapter;
import com.example.kiylx.ti.adapters.bookmarkadapter.TouchMethod;
import com.example.kiylx.ti.model.WebPage_Info;
import com.example.kiylx.ti.mvp.presenter.BookmarkFolderPresenter;
import com.example.kiylx.ti.mvp.contract.BookmarkActivityContract;
import com.example.kiylx.ti.mvp.contract.base.BaseLifecycleObserver;
import com.example.kiylx.ti.mvp.presenter.BookmarkManagerPresenter;
import com.example.kiylx.ti.tool.LogUtil;
import com.example.kiylx.ti.ui.base.BaseRecy_search_ViewActivity;
import com.google.android.material.button.MaterialButton;

/**
 * 创建者 kiylx
 * 创建时间 2020/8/26 9:37
 * packageName：com.example.kiylx.ti.ui.activitys
 * 描述：
 */
public class BookmarkFolderActivity extends BaseRecy_search_ViewActivity implements BookmarkActivityContract, TouchMethod {
    private static final String TAG = "BookmarkActivity2";
    private BookmarkFolderPresenter sFolderManagerPresenter;
    private BookmarkListAdapter adapter;
    private RecyclerView recyclerView;
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
        sFolderManagerPresenter = BookmarkFolderPresenter.getInstance( this, handler);
        setToolbarTitle("选择文件夹", null);
        addButtonView();
        initRecyclerView();
    }

    @Override
    protected int toolbarMenu() {
        return R.menu.bookmarkfolder_toolbar;
    }

    @Override
    protected int searchViewId() {
        //不提供searchview
        return 0;
    }

    /**
     * 初始化recyclerview
     */
    private void initRecyclerView() {
        adapter = new BookmarkListAdapter(sFolderManagerPresenter.getBookmarkFolderList());
        adapter.setTouchMethod(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(adapter);
        //获取数据
        sFolderManagerPresenter.getIndex(BookmarkFolderPresenter.DefaultBookmarkFolder.uuid, true);

    }

    /**
     * 更新界面
     * 包含有文件夹list和书签list的键值对
     */
    @Override
    public void updateUI() {
        if (adapter == null) {
            adapter = new BookmarkListAdapter(sFolderManagerPresenter.getBookmarkFolderList());
            recyclerView.setAdapter(adapter);
        } else {
            adapter.setData(sFolderManagerPresenter.getBookmarkFolderList());
        }            /*List<WebPage_Info> newData = sBookmarkManager.getBookmarkList();
            List<WebPage_Info> oldData = adapter.getData();
            adapter.setData(newData);
            DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new BookmarkCallback(oldData, newData));
            diffResult.dispatchUpdatesTo(adapter);*/
        //不使用差异计算时的测试
        adapter.notifyDataSetChanged();

        for (WebPage_Info info : sFolderManagerPresenter.getBookmarkFolderList()) {
            LogUtil.d(TAG, "开始更新界面，接收到的数据： " + info.getUuid());
        }

    }

    /**
     * 创建handler处理消息，更新界面
     */
    public void createHandler() {
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);updateUI();
            }
        };

    }
    @Override
    protected void ListenItemClick(MenuItem item) {
        super.ListenItemClick(item);
        switch (item.getItemId()) {
            case R.id.addbookmarkfolder:
                sFolderManagerPresenter.createFolder("测试", sFolderManagerPresenter.getCurrentPath());
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
                LogUtil.d(TAG,"被选择的文件夹uuid"+sFolderManagerPresenter.getCurrentPath());
                Intent intent = getIntent();
                intent.putExtra("FolderName", sFolderManagerPresenter.getCurrentPathName());
                intent.putExtra("FatherFolder",sFolderManagerPresenter.getCurrentPath());
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });
    }

    @Override
    public void click_bookmark(View view, WebPage_Info info) {
    }

    @Override
    public boolean onLongClick_bookmark(View view, WebPage_Info info) {
        return true;
    }

    @Override
    public void click_folder(View view, WebPage_Info info) {
        sFolderManagerPresenter.enterFolder(info.getUuid());
        sFolderManagerPresenter.setCurrentPathName(info.getTitle());
    }

    @Override
    public boolean onLongClick_folder(View view, WebPage_Info info) {
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (!sFolderManagerPresenter.getBackStack()) {
                sFolderManagerPresenter.clickBack();
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
