package com.example.kiylx.ti.mvp.presenter;

import android.os.Handler;
import android.widget.Toast;


import com.example.kiylx.ti.conf.SomeRes;
import com.example.kiylx.ti.db.bookmarkdb.bookmark.BookmarkDBcontrol;
import com.example.kiylx.ti.db.bookmarkdb.bookmarkfolder.BookmarkfolderDBcontrol;
import com.example.kiylx.ti.model.BookmarkFolderNode;
import com.example.kiylx.ti.model.WebPage_Info;
import com.example.kiylx.ti.mvp.contract.BookmarkActivityContract;
import com.example.kiylx.ti.mvp.contract.base.BaseContract;
import com.example.kiylx.ti.mvp.presenter.base.BasePresenter;
import com.example.kiylx.ti.tool.LogUtil;
import com.example.kiylx.ti.tool.SomeTools;
import com.example.kiylx.ti.tool.threadpool.SimpleThreadPool;
import com.example.kiylx.ti.xapplication.Xapplication;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.UUID;

/**
 * 创建者 kiylx
 * 创建时间 2020/8/8 22:43
 * packageName：com.example.kiylx.ti.mvp.presenter
 * 描述：
 */
public class BookmarkManagerPresenter extends BasePresenter<BookmarkActivityContract>  {
    protected static final String TAG = "BookmarkManager";
    protected final Handler handler;

    protected Stack<String> backStack;//记录点击的文件夹层级，便于回退到上一级目录
    protected String currentPath;//当前文件夹层级
    protected List<WebPage_Info> bookmarkList;

    //DataBase
    protected BookmarkDBcontrol sBookmarkDBcontrol;
    protected BookmarkfolderDBcontrol sBookmarkfolderDBcontrol;

    //threadpool
    protected SimpleThreadPool threadPool;



    public static BookmarkManagerPresenter getInstance(BaseContract.View view, Handler handler) {
        return new BookmarkManagerPresenter(view, handler);
    }

    protected BookmarkManagerPresenter(BaseContract.View view, Handler handler) {
        super((BookmarkActivityContract) view);
        //初始化数据库控制
        sBookmarkDBcontrol = BookmarkDBcontrol.get(Xapplication.getInstance());
        sBookmarkfolderDBcontrol = BookmarkfolderDBcontrol.get(Xapplication.getInstance());

        currentPath = DefaultBookmarkFolder.uuid;

        backStack = new Stack<>();
        this.handler = handler;
        threadPool = SimpleThreadPool.getInstance();

        bookmarkList = new ArrayList<>();
    }

    /**
     * 点击返回时，回退到上级文件夹层级目录
     */
    public void clickBack() {
        getIndex(getUpperLevel(), true);
    }

    public boolean getBackStack() {
        return backStack.empty();
    }

    /**
     * @param uuid 被点击文件夹的uuid
     *             把currentpath入栈，把传进来的uuid作为新的currentpath，然后更新目录
     */

    public void enterFolder(String uuid) {
        if (backStack == null) {
            backStack = new Stack<>();
        }
        backStack.push(currentPath.trim());
        currentPath = uuid;
        getIndex(uuid, true);
    }

    public String getCurrentPath() {
        return currentPath;
    }

    /**
     * @return 返回当前文件夹的父目录的uuid。
     */
    public String getUpperLevel() {
        if (backStack == null || backStack.empty()) {
            currentPath = BookmarkManagerPresenter.DefaultBookmarkFolder.uuid;
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
        if (bookmarkList == null) {
            bookmarkList = new ArrayList<>();
        } else {
            bookmarkList.clear();
        }
        threadPool.getExecutorService().execute(new Runnable() {
            @Override
            public void run() {
                List<WebPage_Info> result = new ArrayList<>(sBookmarkfolderDBcontrol.queryFolder(uuid, true));
                List<WebPage_Info> result2 = sBookmarkDBcontrol.queryBookmarks(uuid);
                bookmarkList.addAll(result);
                bookmarkList.addAll(result2);
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
     */
    public void deleteFolder(@NotNull String uuid, boolean deleteBookmark) {
        for (WebPage_Info info : bookmarkList) {
            if (info.getUuid().equals(uuid)) {
                bookmarkList.remove(info);
                break;
            }
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

    /**
     * @param infos 待删除的书签的集合
     *              批量删除书签和书签文件夹
     */
    public void deleteBookmarks(List<WebPage_Info> infos) {
        if (infos==null||infos.isEmpty())
            return;
        bookmarkList.removeAll(infos);
        viewContract.updateUI();
        threadPool.getExecutorService().execute(new Runnable() {
            @Override
            public void run() {
                for (WebPage_Info info : infos) {
                    if (info.getWEB_feature() == -2) {
                        deleteFolder(info.getUuid(), true);
                    } else {
                        deleteBookmark(info.getUuid());
                    }
                }

            }
        });
    }

    /**
     * @param infos 待更改层级的书签的集合
     *              批量更改书签和书签文件夹的层级
     */
    public void changeLevel(List<WebPage_Info> infos, String newParentUUID) {
        if (infos==null||infos.isEmpty()){
            LogUtil.showToast("改变层级失败,没有选择");
            return;
        }

        for (WebPage_Info info : infos) {
            if (info.getUuid().equals(newParentUUID)){
                LogUtil.showToast("被移动文件夹不能与目标文件夹相同");
                return;
            }
        }
        threadPool.getExecutorService().execute(new Runnable() {
            @Override
            public void run() {
                for (WebPage_Info info : infos) {
                    if (info.getWEB_feature() != -2) {
                        //书签
                        info.setBookmarkFolderUUID(newParentUUID);
                        sBookmarkDBcontrol.update(info);
                    } else {
                        //文件夹
                        sBookmarkfolderDBcontrol.updateFolderParentuuid(info.getUuid(), newParentUUID);
                    }
                }

                handler.sendEmptyMessage(HandlerMsg.updateUI_flash);
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
        boolean b = bookmarkList == null;
        LogUtil.d(TAG, "数据源是null？ " + b);
        for (WebPage_Info info : bookmarkList) {
            LogUtil.d(TAG, "现有的数据 " + info.getUuid());
        }
        return bookmarkList;
    }


    /**
     * @param uuid 文件夹的uuid
     * @return 返回该uuid文件夹的名称
     */
    public String queryFolderName(String uuid) {
        return null;
    }

    public void destroy() {
        bookmarkList = null;
        currentPath = DefaultBookmarkFolder.uuid;
        backStack = null;
    }

    public void setCurrentPath(String currnetPath) {
        this.currentPath = currnetPath;
    }

    /**
     * 定义默认的书签的根目录
     */
    public static class DefaultBookmarkFolder {
        public static final String folderName = "默认文件夹";
        public static final String uuid = "siefwyrwrklfhiwGFQD";
        public static final String parentUUID = "siefwyrwrklfhiwGFQD";
    }

    public static class HandlerMsg {
        public static final int updateUI_bookmark_folder = 200;
        public static final int updateUI_flash = 201;
    }
}
