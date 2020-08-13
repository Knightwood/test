package com.example.kiylx.ti.ui.activitys;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;


import androidx.recyclerview.widget.DiffUtil;

import com.example.kiylx.ti.R;
import com.example.kiylx.ti.db.bookmarkdb.bookmark.BookmarkListAdapter;
import com.example.kiylx.ti.model.WebPage_Info;
import com.example.kiylx.ti.mvp.contract.BookmarkActivityContract;
import com.example.kiylx.ti.mvp.contract.base.BaseLifecycleObserver;
import com.example.kiylx.ti.mvp.presenter.BookmarkManager;
import com.example.kiylx.ti.ui.base.BaseRecy_search_ViewActivity;
import com.example.kiylx.ti.xapplication.Xapplication;

import java.util.List;

public class BookmarkManagerActivity extends BaseRecy_search_ViewActivity implements BookmarkActivityContract {
    BookmarkManager bookmarkManager;
    BookmarkListAdapter adapter;

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
        List<WebPage_Info> oldData=adapter.getData();
        List<WebPage_Info> newList=folderAndBookmark;
        adapter.setData(newList);
        DiffUtil.DiffResult diffResult=DiffUtil.calculateDiff(new BookmarkCallback(oldData,newList));
        diffResult.dispatchUpdatesTo(adapter);
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

    class BookmarkCallback extends DiffUtil.Callback{
        private List<WebPage_Info> oldData;
        private List<WebPage_Info> newData;

        public BookmarkCallback(List<WebPage_Info> oldData, List<WebPage_Info> newData) {
            this.oldData = oldData;
            this.newData = newData;
        }

        @Override
        public int getOldListSize() {
            return oldData==null? 0:oldData.size();
        }

        @Override
        public int getNewListSize() {
            return newData==null? 0:newData.size();
        }
        // 判断是不是同一个Item：如果Item有唯一标志的Id的话，此处判断uuid
        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            WebPage_Info oldInfo=oldData.get(oldItemPosition);
            WebPage_Info newInfo=newData.get(newItemPosition);
            return oldInfo.getUuid().equals(newInfo.getUuid());
        }

        //此处判断标题、网址、书签所属文件夹,以此得到内容有没有发生变化
        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            WebPage_Info oldInfo=oldData.get(oldItemPosition);
            WebPage_Info newInfo=newData.get(newItemPosition);
            if (!oldInfo.getTitle().equals(newInfo.getTitle())){//标题比较
                return false;
            }
            if (!oldInfo.getBookmarkFolderUUID().equals(newInfo.getBookmarkFolderUUID())){//所属的文件夹的uuid比较
                return false;
            }
            if (!oldInfo.getUrl().equals(newInfo.getUrl())){//本身的网址变化
                return false;
            }
            return true;
        }
    }

}
