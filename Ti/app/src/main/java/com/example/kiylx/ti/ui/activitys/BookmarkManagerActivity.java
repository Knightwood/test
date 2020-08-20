package com.example.kiylx.ti.ui.activitys;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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
import com.example.kiylx.ti.mvp.presenter.BookmarkManager;
import com.example.kiylx.ti.tool.LogUtil;
import com.example.kiylx.ti.ui.base.BaseRecy_search_ViewActivity;
import com.example.kiylx.ti.xapplication.Xapplication;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BookmarkManagerActivity extends BaseRecy_search_ViewActivity implements BookmarkActivityContract {
    private static final String TAG = "BookmarkActivity2";
    private static OpenOneWebpage mopenWeb;//用于调用mainactivity中的接口
    private BookmarkManager sBookmarkManager;
    private BookmarkListAdapter adapter;
    private RecyclerView recyclerView;
    private static boolean containBookmarks = true;//true则显示书签和文件夹。false则是只显示文件夹，这时就是选择文件夹模式


    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        recyclerView = findViewById(R.id.base_search_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void initActivity(BaseLifecycleObserver observer) {
        sBookmarkManager = BookmarkManager.getInstance(Xapplication.getInstance(), this);
        setToolbarTitle("收藏夹", null);
        if (getIntent() != null) {
            containBookmarks = getIntent().getBooleanExtra("isShowBookmarks", true);
            if (!containBookmarks) {
                addButtonView();
            }
        }
//initTest();
    }

    @Override
    protected void initViewAfter(Bundle savedInstanceState) {
        super.initViewAfter(savedInstanceState);
        initRecyclerView();
    }

    private void initRecyclerView() {
        adapter = new BookmarkListAdapter(null);
        recyclerView.setAdapter(adapter);

        if (sBookmarkManager.getBookmarkList() == null || sBookmarkManager.getBookmarkList().isEmpty()) {
            //从manager中拿数据，若是数据是null，那就从数据库里读取在更新界面，否则直接用数据更新界面
            //若是我每次都用getIndex()方法从数据库拿数据来更新界面，在第二次打开activity时就会什么也不显示，但明明已经拿到了数据，我猜测可能与线程调度与activity的生命周期有问题
            sBookmarkManager.getIndex(BookmarkManager.DefaultBookmarkFolder.uuid, containBookmarks);
        } else {
            updateUI(sBookmarkManager.getBookmarkList());
        }
        /*updateUI(testList);*/
    }

    public static void setInterface(OpenOneWebpage minterface) {
        BookmarkManagerActivity.mopenWeb = minterface;
    }

    /**
     * 更新界面
     *
     * @param folderAndBookmark 包含有文件夹list和书签list的键值对
     */
    @Override
    public void updateUI(List<WebPage_Info> folderAndBookmark) {
        showToast("接收到了更新");
        if (adapter == null) {
            if (folderAndBookmark == null || folderAndBookmark.isEmpty()) {
                adapter = new BookmarkListAdapter(new ArrayList<>());
            } else {
                adapter = new BookmarkListAdapter(folderAndBookmark);
            }

            recyclerView.setAdapter(adapter);
        } else {
            List<WebPage_Info> oldData = adapter.getData();
            adapter.setData(folderAndBookmark);
            DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new BookmarkCallback(oldData, folderAndBookmark));
            diffResult.dispatchUpdatesTo(adapter);
            /*adapter.setData(folderAndBookmark);
            adapter.notifyDataSetChanged();*/
        }
        for (WebPage_Info info : folderAndBookmark) {
            LogUtil.d(TAG, "开始更新界面，接收到的数据： " + info.getUuid());
        }

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
    protected void ListenItemClick(MenuItem item) {
        super.ListenItemClick(item);
        switch (item.getItemId()) {
            case 1:

                break;
        }
    }

    List<WebPage_Info> testList;

    public void initTest() {
        WebPage_Info info = new WebPage_Info.Builder("www.baidu.com")
                .title("测试")
                .uuid(UUID.randomUUID().toString())
                .bookmarkFolderUUID(BookmarkManager.DefaultBookmarkFolder.uuid)
                .build();
        WebPage_Info info2 = new WebPage_Info.Builder("www.baidu.com")
                .title("测试")
                .uuid(UUID.randomUUID().toString())
                .bookmarkFolderUUID(BookmarkManager.DefaultBookmarkFolder.uuid)
                .build();
        if (testList == null) {
            testList = new ArrayList<>();
        }
        testList.add(info);
        testList.add(info2);
    }

    class BookmarkCallback extends DiffUtil.Callback {
        private List<WebPage_Info> oldData;
        private List<WebPage_Info> newData;

        public BookmarkCallback(List<WebPage_Info> oldData, List<WebPage_Info> newData) {
            this.oldData = oldData;
            this.newData = newData;
            for (WebPage_Info info :
                    oldData) {
                LogUtil.d(TAG, "旧数据：" + info.getUuid());
            }
            for (WebPage_Info info :
                    newData) {
                LogUtil.d(TAG, "新数据：" + info.getUuid());
            }
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
                intent.putExtra("FolderName", sBookmarkManager.queryFolderName(sBookmarkManager.getUpperLevel()));
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
                            Intent i = EditBookmarkActivity.newInstance(info, getApplicationContext(), sBookmarkManager.queryFolderName(info.getBookmarkFolderUUID()));
                            startActivityForResult(i, ActivityCode.editBookmarkCode);
                            break;
                        case R.id.delete_Bookmark:
                            sBookmarkManager.deleteBookmark(info.getUuid());
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
}
