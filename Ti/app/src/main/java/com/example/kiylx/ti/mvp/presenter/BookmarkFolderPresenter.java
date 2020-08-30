package com.example.kiylx.ti.mvp.presenter;

import android.os.Handler;

import com.example.kiylx.ti.model.BookmarkFolderNode;
import com.example.kiylx.ti.model.WebPage_Info;
import com.example.kiylx.ti.mvp.contract.base.BaseContract;
import com.example.kiylx.ti.tool.LogUtil;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * 创建者 kiylx
 * 创建时间 2020/8/29 15:42
 * packageName：com.example.kiylx.ti.mvp
 * 描述：继承自 BookmarkManagerPresenter 改写getindex方法，以此只提供书签文件夹list，而不是书签和书签文件夹的list
 */
public class BookmarkFolderPresenter extends BookmarkManagerPresenter {
    private String currentPathName;//当前路径的文件夹名称

    public static BookmarkFolderPresenter getInstance(BaseContract.View view, Handler handler) {
        return new BookmarkFolderPresenter(view,handler);
    }

    private BookmarkFolderPresenter(BaseContract.View view, Handler handler) {
        super(view,handler);
    }

    @Override
    public void getIndex(@NotNull String uuid, boolean b) {
        threadPool.getExecutorService().execute(new Runnable() {
            @Override
            public void run() {
                List<BookmarkFolderNode> folders = sBookmarkfolderDBcontrol.queryFolder(uuid, true);

                if (bookmarkList==null){
                    bookmarkList=new ArrayList<>();
                }else{
                    bookmarkList.clear();
                }
                bookmarkList.addAll(folders);
                if (b) {
                    handler.sendEmptyMessage(HandlerMsg.updateUI_bookmark_folder);
                }
            }
        });
    }

    /**
     * @param uuid 文件夹的uuid
     * @return 返回该uuid文件夹的名称
     */
    @Override
    public String queryFolderName(String uuid) {
        if (bookmarkList == null ||currentPath.equals(DefaultBookmarkFolder.uuid)) {
            LogUtil.d(TAG,"查询文件夹名称"+currentPath);
            return DefaultBookmarkFolder.folderName;
        }
        for (WebPage_Info info : bookmarkList) {
            if ((info.getUuid()).equals(currentPath)) {
                return info.getTitle();
            }
        }
        return "nul";
    }

    /**
     * @return 返回已经拿到的文件夹信息，此列表只包含文件夹
     */
    public List<WebPage_Info> getBookmarkFolderList() {
        return bookmarkList;
    }

    public String getCurrentPathName() {
        return currentPathName;
    }

    /**
     * @param currentPathName 当前文件夹的名称
     */
    public void setCurrentPathName(String currentPathName) {
        this.currentPathName = currentPathName;
    }
}
