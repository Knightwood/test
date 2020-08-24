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

import com.crystal.customview.baseadapter1.BaseAdapter;
import com.crystal.customview.baseadapter1.BaseHolder;
import com.example.kiylx.ti.R;
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

public class BookmarkManagerActivity extends BaseRecy_search_ViewActivity implements BookmarkActivityContract {
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
        if (containBookmarks)
           { adapter = new BookmarkListAdapter(sBookmarkManagerPresenter.getBookmarkList());}
        else
          {  adapter = new BookmarkListAdapter(sBookmarkManagerPresenter.getBookmarkFolderList());}
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
            adapter = new BookmarkListAdapter(sBookmarkManagerPresenter.getBookmarkList());
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

    class BookmarkCallback extends DiffUtil.Callback {
        private List<WebPage_Info> oldData;
        private List<WebPage_Info> newData;

        public BookmarkCallback(List<WebPage_Info> oldData, List<WebPage_Info> newData) {
            this.oldData = oldData;
            this.newData = newData;
        }

        @Override
        public int getOldListSize() {
            return oldData == null ? 0 : oldData.size();
        }

        @Override
        public int getNewListSize() {
            return newData == null ? 0 : newData.size();
        }

        // 判断是不是同一个Item：如果Item有唯一标志的Id的话，此处判断uuid
        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            WebPage_Info oldInfo = oldData.get(oldItemPosition);
            WebPage_Info newInfo = newData.get(newItemPosition);
            String oldInfoUUID = oldInfo.getUuid();
            String newInfoUUID = newInfo.getUuid();
            if (oldInfoUUID == null || newInfoUUID == null)
                throw new NullPointerException("uuid不能为null");
            return oldInfo.getUuid().equals(newInfo.getUuid());
        }

        //此处判断标题、网址、书签所属文件夹,以此得到内容有没有发生变化
        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            WebPage_Info oldInfo = oldData.get(oldItemPosition);
            WebPage_Info newInfo = newData.get(newItemPosition);
            LogUtil.d(TAG, "旧标题：新标题" + oldInfo.getTitle() + " : " + newInfo.getTitle());
            LogUtil.d(TAG, "UUID，旧：新" + oldInfo.getUuid() + " : " + newInfo.getUuid());
            if (!oldInfo.getTitle().equals(newInfo.getTitle())) {//标题比较
                return false;
            }
            if (!oldInfo.getBookmarkFolderUUID().equals(newInfo.getBookmarkFolderUUID())) {//所属的文件夹的uuid比较
                return false;
            }
            if (!oldInfo.getUrl().equals(newInfo.getUrl())) {//本身的网址变化
                return false;
            }
            return true;
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

    public class BookmarkListAdapter extends BaseAdapter<WebPage_Info, BaseHolder<WebPage_Info>> {
        public BookmarkListAdapter(List<WebPage_Info> list) {
            super(list);
        }

        @Override
        protected int getItemViewResId(int viewType) {
            switch (viewType) {
                case BookmarkKind.folder:
                    break;
                case BookmarkKind.bookmark:
                    break;
            }
            return R.layout.history_item;
        }

        @Override
        public int getViewType(WebPage_Info bean) {
            return bean.getWEB_feature() == -2 ? BookmarkKind.folder : BookmarkKind.bookmark;
        }

        @Override
        public BaseHolder<WebPage_Info> createHolder(View itemView, int viewType) {
            return new BaseHolder<>(itemView);
        }

        @Override
        public void bind(BaseHolder<WebPage_Info> holder, WebPage_Info data, int viewType) {
            switch (viewType) {
                case BookmarkKind.folder:
                    holder.setImageResource(R.id.Bookmarkimage, R.drawable.ic_folder_black_24dp)
                            .setText(R.id.itemTitle, data.getTitle())
                            .setImageResource(R.id.more_setting, R.drawable.chevronright);

                    holder.setOnIntemClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    }, data);
                    break;
                case BookmarkKind.bookmark:
                    holder.setImageResource(R.id.Bookmarkimage, R.drawable.ic_bookmark_black_24dp)
                            .setText(R.id.itemTitle, data.getTitle())
                            .setText(R.id.itemurl, data.getUrl())
                            .setImageResource(R.id.more_setting, R.drawable.morevert);
                    holder.setOnIntemClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            switch (v.getId()) {
                                case R.id.itemTitle:
                                    //点击item后访问网址
                                    finish();
                                    mopenWeb.loadUrl(data.getUrl(), false);
                                    break;
                                case R.id.more_setting:
                                    addPopMenu(v, data);
                                    break;
                            }
                        }
                    }, data);
                    break;
            }

        }

        /**
         * @param v    要添加popmenu的视图
         * @param info info
         */
        void addPopMenu(View v, WebPage_Info info) {
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
                            //因为mainactivity里加载网页代码太烂，这里写true用新标签页打开会有bug
                            mopenWeb.loadUrl(info.getUrl(), true);
                    }
                    return false;
                }
            });
            popupMenu.show();

        }

        class BookmarkKind {
            public static final int folder = 1;
            public static final int bookmark = 2;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
