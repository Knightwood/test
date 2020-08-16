package com.example.kiylx.ti.ui.activitys;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;


import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.crystal.customview.baseadapter1.BaseAdapter;
import com.crystal.customview.baseadapter1.BaseHolder;
import com.example.kiylx.ti.R;
import com.example.kiylx.ti.conf.SomeRes;
import com.example.kiylx.ti.db.bookmarkdb.bookmark.BookmarkListAdapter;
import com.example.kiylx.ti.model.WebPage_Info;
import com.example.kiylx.ti.mvp.contract.BookmarkActivityContract;
import com.example.kiylx.ti.mvp.contract.base.BaseLifecycleObserver;
import com.example.kiylx.ti.mvp.presenter.BookmarkManager;
import com.example.kiylx.ti.tool.LogUtil;
import com.example.kiylx.ti.ui.base.BaseRecy_search_ViewActivity;
import com.example.kiylx.ti.xapplication.Xapplication;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BookmarkManagerActivity extends BaseRecy_search_ViewActivity implements BookmarkActivityContract {
    private static final String TAG = "BookmarkActivity2";
    private BookmarkManager sBookmarkManager;
    private BookmarkListAdapter adapter;
    private RecyclerView recyclerView;

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        recyclerView = findViewById(R.id.histories_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void initActivity(BaseLifecycleObserver observer) {
        sBookmarkManager = BookmarkManager.getInstance(Xapplication.getInstance(), this);
        setToolbarTitle("收藏夹", null);
        initTest();

    }

    @Override
    protected void initViewAfter(Bundle savedInstanceState) {
        super.initViewAfter(savedInstanceState);
        initRecyclerView();
    }

    private void initRecyclerView() {
        adapter = new BookmarkListAdapter(null);
        recyclerView.setAdapter(adapter);

        if (sBookmarkManager.getBookmarkList() == null) {
            //从manager中拿数据，若是数据是null，那就从数据库里读取在更新界面，否则直接用数据更新界面
            //若是我每次都用getIndex()方法从数据库拿数据来更新界面，在第二次打开activity时就会什么也不显示，但明明已经拿到了数据，我猜测可能与线程调度与activity的生命周期有问题
            sBookmarkManager.getIndex(SomeRes.defaultBookmarkFolderUUID);
        } else {
            UpdateUI(sBookmarkManager.getBookmarkList());
        }
    }

    /**
     * 更新界面
     *
     * @param folderAndBookmark 包含有文件夹list和书签list的键值对
     */
    @Override
    public void UpdateUI(List<WebPage_Info> folderAndBookmark) {
        showToast("接收到了更新");
        for (WebPage_Info info :
                folderAndBookmark) {
            LogUtil.d(TAG, "开始更新界面，接收到的数据： " + info.getUuid());
        }
        if (adapter == null) {
            if (folderAndBookmark == null || folderAndBookmark.isEmpty()) {
                adapter = new BookmarkListAdapter(new ArrayList<>());
            } else {
                adapter = new BookmarkListAdapter(folderAndBookmark);
            }

            recyclerView.setAdapter(adapter);
        } else {
           /* List<WebPage_Info> oldData = adapter.getData();
            adapter.setData(folderAndBookmark);
            DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new BookmarkCallback(oldData, folderAndBookmark));
            diffResult.dispatchUpdatesTo(adapter);*/
            adapter.setData(folderAndBookmark);
            adapter.notifyDataSetChanged();
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
    protected void ListenItem(MenuItem item) {
        super.ListenItem(item);
        switch (item.getItemId()) {
            case 1:
                sBookmarkManager.createFolder("xnnnn", SomeRes.defaultBookmarkFolderUUID);
                WebPage_Info info = new WebPage_Info.Builder("www.baidu.com")
                        .title("测试")
                        .uuid(UUID.randomUUID().toString())
                        .bookmarkFolderUUID(SomeRes.defaultBookmarkFolderUUID)
                        .build();
                sBookmarkManager.insertBookmark(info);
                break;
        }
    }

    List<WebPage_Info> testList;

    public void initTest() {
        WebPage_Info info = new WebPage_Info.Builder("www.baidu.com")
                .title("测试")
                .uuid(UUID.randomUUID().toString())
                .bookmarkFolderUUID(SomeRes.defaultBookmarkFolderUUID)
                .build();
        WebPage_Info info2 = new WebPage_Info.Builder("www.baidu.com")
                .title("测试")
                .uuid(UUID.randomUUID().toString())
                .bookmarkFolderUUID(SomeRes.defaultBookmarkFolderUUID)
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


}
