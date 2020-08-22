package com.example.kiylx.ti.mvp.presenter;

import android.content.Context;
import android.os.Handler;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.kiylx.ti.db.bookmarkdb.bookmark.BookmarkDBcontrol;
import com.example.kiylx.ti.db.bookmarkdb.bookmarkfolder.BookmarkfolderDBcontrol;
import com.example.kiylx.ti.model.BookmarkFolderNode;
import com.example.kiylx.ti.model.WebPage_Info;
import com.example.kiylx.ti.mvp.contract.BookmarkActivityContract;
import com.example.kiylx.ti.mvp.contract.base.BaseContract;
import com.example.kiylx.ti.mvp.presenter.base.BasePresenter;
import com.example.kiylx.ti.tool.LogUtil;
import com.example.kiylx.ti.tool.threadpool.SimpleThreadPool;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.UUID;
import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * 创建者 kiylx
 * 创建时间 2020/8/8 22:43
 * packageName：com.example.kiylx.ti.mvp.presenter
 * 描述：
 */
public class BookmarkManager extends BasePresenter<BookmarkActivityContract> {
    private static final String TAG = "BookmarkManager";
    private static volatile BookmarkManager sBookmarkManager;
    private final Handler handler;

    private Stack<String> backStack;//记录点击的文件夹层级，便于回退到上一级目录
    private String currentPath;//当前文件夹层级

    private List<WebPage_Info> bookmarkList;
    private List<WebPage_Info> bookmarkFolderList;

    //DataBase
    private BookmarkDBcontrol sBookmarkDBcontrol;
    private BookmarkfolderDBcontrol sBookmarkfolderDBcontrol;

    //threadpool
    private SimpleThreadPool threadPool;

    public static BookmarkManager getInstance(Context context, BaseContract.View view, Handler handler) {
        if (sBookmarkManager == null) {
            synchronized (BookmarkManager.class) {
                if (sBookmarkManager == null) {
                    sBookmarkManager = new BookmarkManager(context, view, handler);
                }
            }
        }
        return sBookmarkManager;
    }

    private BookmarkManager(Context context, BaseContract.View view, Handler handler) {
        super((BookmarkActivityContract) view);
        //初始化数据库控制
        sBookmarkDBcontrol = BookmarkDBcontrol.get(context);
        sBookmarkfolderDBcontrol = BookmarkfolderDBcontrol.get(context);

        currentPath = DefaultBookmarkFolder.uuid;
        this.handler = handler;
        threadPool = SimpleThreadPool.getInstance();

        //初始化书签及文件夹list和文件夹list
        getIndex(DefaultBookmarkFolder.uuid, false);
        getBookmarkFolderList(DefaultBookmarkFolder.uuid, false);
    }

    /**
     * 点击返回时，回退到上级文件夹层级目录
     *
     * @param containBookmark 最终的结果是否包含书签记录
     */
    public void clickBack(boolean containBookmark) {
        if (containBookmark) {
            getIndex(getUpperLevel(), true);
        } else {
            getBookmarkFolderList(getUpperLevel(), true);
        }
    }

    /**
     * @param uuid            被点击文件夹的uuid
     * @param containBookmark true则结果中包含有书签记录，否则只包含文件夹
     *                        获取被点击文件夹的子目录
     */
    public void enterFolder(String uuid, boolean containBookmark) {
        backStack.push(currentPath);
        currentPath = uuid;
        if (containBookmark)
            getIndex(uuid, true);
        else
            getBookmarkFolderList(uuid, true);

    }

    /**
     * @return 返回当前文件夹的父目录的uuid。
     */
    public String getUpperLevel() {
        if (backStack == null || backStack.empty()) {
            currentPath = BookmarkManager.DefaultBookmarkFolder.uuid;
        } else {
            currentPath = backStack.pop();
        }
        return currentPath;
    }

    /**
     * @param uuid 当前文件夹的uuid
     *             从数据库读取当前文件夹的子文件夹和书签记录，放在同一个list中，并更新界面
     * @param b    true则触发更新界面，false则不做更新界面处理
     */
    public void getIndex(@NotNull String uuid, boolean b) {
        threadPool.getExecutorService().execute(new Runnable() {
            @Override
            public void run() {
                List<BookmarkFolderNode> folders = sBookmarkfolderDBcontrol.queryFolder(uuid, true);
                List<WebPage_Info> result = new ArrayList<>(folders);
                result.addAll(sBookmarkDBcontrol.queryBookmarks(uuid));
                if (bookmarkList==null){
                    bookmarkList=new ArrayList<>();
                }else{
                    bookmarkList.clear();
                }
                bookmarkList.addAll(result);
                if (b) {
                    handler.sendEmptyMessage(HandlerMsg.updateUI_bookmark_folder);
                }

            }
        });
    }

    /**
     * @param name       将要创建的文件夹名称
     * @param parentUUID 文件夹所属的父级文件夹
     */
    public void createFolder(@NotNull String name, @NotNull String parentUUID) {
        WebPage_Info folderInfo = new BookmarkFolderNode(name, UUID.randomUUID().toString(), parentUUID);
        bookmarkList.add(folderInfo);
        bookmarkFolderList.add(folderInfo);

        viewContract.updateUI();

        threadPool.getExecutorService().execute(new Runnable() {
            @Override
            public void run() {
                sBookmarkfolderDBcontrol.insertNode((BookmarkFolderNode) folderInfo);
            }
        });
    }

    /**
     * @param uuid           将被删的文件夹的uuid
     * @param deleteBookmark 是否同时删除该文件夹下的书签,
     *                       true则删除所有该文件夹下的书签
     *                       false则将该文件夹下的书签迁移到根目录
     */
    public void deleteFolder(@NotNull String uuid, boolean deleteBookmark) {
        for (WebPage_Info info : bookmarkList) {
            if (info.getUuid().equals(uuid)) {
                bookmarkList.remove(info);
                bookmarkFolderList.remove(info);
                break;
            }
            viewContract.updateUI();

            threadPool.getExecutorService().execute(new Runnable() {
                @Override
                public void run() {
                    sBookmarkfolderDBcontrol.deleteFolder(uuid);
                    sBookmarkDBcontrol.delete(uuid, deleteBookmark);
                    if (!deleteBookmark) {
                        sBookmarkDBcontrol.updateFolderUUID(uuid, DefaultBookmarkFolder.uuid);
                    }
                }
            });

        }
    }

    /**
     * @param uuid 待删除书签的uuid
     *             根据uuid删除数据库中的书签
     */
    public void deleteBookmark(String uuid) {
        int i = 0;
        while (i < bookmarkList.size()) {
            if (bookmarkList.get(i).getUuid().equals(uuid)) {
                bookmarkList.remove(i);
                break;
            }
            i++;
        }
        viewContract.updateUI();

        threadPool.getExecutorService().execute(new Runnable() {
            @Override
            public void run() {
                sBookmarkDBcontrol.delete(uuid, false);
            }
        });
    }

    public void changeFolderName(@NotNull String uuid, @NotNull String newName) {
        int i = 0;
        WebPage_Info info;
        while (i < bookmarkList.size()) {
            info = bookmarkList.get(i);
            if (info.getUuid().equals(uuid)) {
                info.setTitle(newName);
                break;
            }
            i++;
        }


        viewContract.updateUI();

        threadPool.getExecutorService().execute(new Runnable() {
            @Override
            public void run() {
                sBookmarkfolderDBcontrol.updateFolderName(uuid, newName);
            }
        });
    }

    /**
     * @param uuid          文件夹本身的uuid
     * @param newParentUuid 将文件夹迁移到另一个的文件夹下，这是另一个文件夹的uuid
     */
    public void changeFolderLevel(@NotNull String uuid, @NotNull String newParentUuid) {
        int i = 0;
        WebPage_Info info;
        while (i < bookmarkList.size()) {
            info = bookmarkList.get(i);
            if (info.getUuid().equals(uuid)) {
                bookmarkList.remove(info);
                break;
            }
            i++;
        }
        viewContract.updateUI();

        threadPool.getExecutorService().execute(new Runnable() {
            @Override
            public void run() {
                sBookmarkfolderDBcontrol.updateFolderParentuuid(uuid, newParentUuid);
                LogUtil.d(TAG, "被修改层级的文件夹uuid：");
            }
        });
    }


    /**
     * @param info 即将插入数据库的书签信息
     *             将书签信息插入数据库
     */
    public void insertBookmark(WebPage_Info info) {
        sBookmarkDBcontrol.insertBookmark(info);
    }

    /**
     * @return 返回已经拿到的书签列表信息，此列表包含书签和文件夹
     */
    public List<WebPage_Info> getBookmarkList() {
        return bookmarkList;
    }

    /**
     * @return 返回已经拿到的书签文件夹列表信息，此列表只包含书签文件夹
     */
    public List<WebPage_Info> getBookmarkFolderList(String uuid, boolean b) {

        threadPool.getExecutorService().execute(new Runnable() {
            @Override
            public void run() {
                List<BookmarkFolderNode> folders = sBookmarkfolderDBcontrol.queryFolder(uuid, true);
                List<WebPage_Info> result = new ArrayList<>(folders);

                if (bookmarkFolderList==null){
                    bookmarkFolderList=new ArrayList<>();
                }else{
                    bookmarkFolderList.clear();
                }
                bookmarkFolderList.addAll(result);
                if (b) {
                    handler.sendEmptyMessage(HandlerMsg.updateUI_folder);
                }
            }
        });
        return bookmarkFolderList;
    }

    /**
     * @param uuid 文件夹的uuid
     * @return 返回该uuid文件夹的名称
     */
    public String queryFolderName(String uuid) {
        if (bookmarkFolderList == null || bookmarkFolderList.isEmpty()) {
            getBookmarkFolderList(DefaultBookmarkFolder.uuid, false);
        }
        for (WebPage_Info info : bookmarkFolderList) {
            if (((BookmarkFolderNode) info).getUUID().equals(uuid)) {
                return ((BookmarkFolderNode) info).getFolderName();
            }
        }
        return " ";
    }

    public void destroy() {
        bookmarkFolderList = null;
        bookmarkList = null;
        sBookmarkDBcontrol.destroy();
        sBookmarkfolderDBcontrol.destroy();
        sBookmarkDBcontrol = null;
        sBookmarkfolderDBcontrol = null;
        sBookmarkManager=null;
    }

    /**
     * 定义默认的书签的根目录
     */
    public static class DefaultBookmarkFolder {
        public static final String folderName = "默认文件夹";
        public static final String uuid = "siefwyrwrklfhiwGFQD";
        public static final String parentUUID = null;
    }

    public static class HandlerMsg {
        public static final int updateUI_bookmark_folder = 200;
        public static final int updateUI_folder = 201;
    }
}
