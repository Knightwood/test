package com.example.kiylx.ti.adapters.bookmarkadapter;

import androidx.recyclerview.widget.DiffUtil;

import com.example.kiylx.ti.model.WebPage_Info;
import com.example.kiylx.ti.tool.LogUtil;
import com.example.kiylx.ti.ui.activitys.BookmarkManagerActivity;

import java.util.List;

/**
 * 创建者 kiylx
 * 创建时间 2020/8/25 7:25
 * packageName：com.example.kiylx.ti.adapters.bookmarkadapter
 * 描述：
 */
public
class BookmarkCallback extends DiffUtil.Callback {
    private static final String TAG = "BookmarkActivity2";

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
