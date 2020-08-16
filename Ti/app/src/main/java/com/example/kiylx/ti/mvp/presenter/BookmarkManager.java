package com.example.kiylx.ti.mvp.presenter;

import android.content.Context;


import com.example.kiylx.ti.conf.SomeRes;
import com.example.kiylx.ti.db.bookmarkdb.bookmark.BookmarkDBcontrol;
import com.example.kiylx.ti.db.bookmarkdb.bookmarkfolder.BookmarkfolderDBcontrol;
import com.example.kiylx.ti.model.BookmarkFolderNode;
import com.example.kiylx.ti.model.WebPage_Info;
import com.example.kiylx.ti.mvp.contract.BookmarkActivityContract;
import com.example.kiylx.ti.mvp.contract.base.BaseContract;
import com.example.kiylx.ti.mvp.presenter.base.BasePresenter;
import com.example.kiylx.ti.tool.LogUtil;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.UUID;
import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
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

    private String currentFolderUUID;//当前展示的所有内容所属的父类的uuid。
    private List<WebPage_Info> bookmarkList;
    private List<BookmarkFolderNode> bookmarkFolderList;
    private Stack<String> folderStack;//记录点击进入的文件夹层级，栈顶是当前文件夹的uuid，随后的是当前文件夹的父级

    //DataBase
    private BookmarkDBcontrol sBookmarkDBcontrol;
    private BookmarkfolderDBcontrol sBookmarkfolderDBcontrol;

    public static BookmarkManager getInstance(Context context, BaseContract.View view) {
        if (sBookmarkManager == null) {
            synchronized (BookmarkManager.class) {
                if (sBookmarkManager == null) {
                    sBookmarkManager = new BookmarkManager(context, view);
                }
            }
        }
        return sBookmarkManager;
    }

    private BookmarkManager(Context context, BaseContract.View view) {
        super((BookmarkActivityContract) view);
        sBookmarkDBcontrol = BookmarkDBcontrol.get(context);
        sBookmarkfolderDBcontrol = BookmarkfolderDBcontrol.get(context);
    }

    public void back() {

    }

    /**
     * @param uuid 当前文件夹的uuid
     *             从数据库读取文件夹和书签记录，放在同一个list中，并更新界面
     */
    public void getIndex(@NotNull String uuid) {
        Observable.create((ObservableOnSubscribe<List<WebPage_Info>>) emitter -> {
            List<BookmarkFolderNode> folders = sBookmarkfolderDBcontrol.queryFolder(uuid, true);
            List<WebPage_Info> result = new ArrayList<>();
            result.addAll(folders);
            result.addAll(sBookmarkDBcontrol.queryBookmarks(uuid));
            emitter.onNext(result);
            emitter.onComplete();
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<WebPage_Info>>() {
                    private Disposable d;

                    @Override
                    public void onSubscribe(Disposable d) {
                        this.d = d;
                    }

                    @Override
                    public void onNext(List<WebPage_Info> webPage_infos) {
                        for (WebPage_Info in : webPage_infos) {
                            LogUtil.d(TAG, "rxjava拿到的数据uuid:" + in.getUuid());
                        }
                        bookmarkList = webPage_infos;
                        viewContract.UpdateUI(bookmarkList);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        d.dispose();
                    }
                });
    }

    public List<BookmarkFolderNode> getFolderList(String uuid) {
        Observable.create((ObservableOnSubscribe<List<BookmarkFolderNode>>) emitter -> {
            emitter.onNext(sBookmarkfolderDBcontrol.queryFolder(uuid, true));
            emitter.onComplete();
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<BookmarkFolderNode>>() {
                    private Disposable d;

                    @Override
                    public void onSubscribe(Disposable d) {
                        this.d = d;
                    }

                    @Override
                    public void onNext(List<BookmarkFolderNode> bookmarkFolders) {
                        for (WebPage_Info in : bookmarkFolders) {
                            LogUtil.d(TAG, "rxjava拿到的数据uuid:" + in.getUuid());
                        }
                        bookmarkFolderList = bookmarkFolders;
                        viewContract.UpdateUI(bookmarkList);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        d.dispose();
                    }
                });
        return getBookmarkFolderList();
    }


    /**
     * @param name       将要创建的文件夹名称
     * @param parentUUID 文件夹所属的父级文件夹
     * @return 返回创建并插入数据库的文件夹node
     */
    public BookmarkFolderNode createFolder(@NotNull String name, @NotNull String parentUUID) {
        final BookmarkFolderNode[] node = {null};
        Observable.fromCallable(new Callable<BookmarkFolderNode>() {

            @Override
            public BookmarkFolderNode call() throws Exception {
                return sBookmarkfolderDBcontrol.insertNode(new BookmarkFolderNode(name, UUID.randomUUID().toString(), parentUUID));
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<BookmarkFolderNode>() {
                    @Override
                    public void accept(BookmarkFolderNode bookmarkFolderNode) throws Exception {
                        node[0] = bookmarkFolderNode;
                    }
                });
        return node[0];

    }

    /**
     * @param uuid           将被删的文件夹的uuid
     * @param deleteBookmark 是否同时删除该文件夹下的书签,
     *                       true则删除所有该文件夹下的书签
     *                       false则将该文件夹下的书签迁移到根目录
     */
    public void deleteFolder(@NotNull String uuid, boolean deleteBookmark) {
        Observable.fromCallable(new Callable<String>() {
            @Override
            public String call() throws Exception {
                sBookmarkfolderDBcontrol.deleteFolder(uuid);
                sBookmarkDBcontrol.delete(uuid, deleteBookmark);
                if (!deleteBookmark) {
                    sBookmarkDBcontrol.updateFolderUUID(uuid, SomeRes.defaultBookmarkFolderUUID);
                }
                return uuid;
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        LogUtil.d(TAG, "删除的文件夹：" + s);
                    }
                });
    }

    public void changeFolderName(@NotNull String uuid, @NotNull String newName) {
        Observable.fromCallable(new Callable<String>() {
            @Override
            public String call() throws Exception {
                sBookmarkfolderDBcontrol.updateFolderName(uuid, newName);
                return uuid;
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        LogUtil.d(TAG, "被修改名称的文件夹uuid：" + s);
                    }
                });
    }

    /**
     * @param uuid          文件夹本身的uuid
     * @param newParentUuid 将文件夹迁移到另一个的文件夹下，这是另一个文件夹的uuid
     */
    public void changeFolderLevel(@NotNull String uuid, @NotNull String newParentUuid) {
        Observable.fromCallable(new Callable<String>() {
            @Override
            public String call() throws Exception {
                sBookmarkfolderDBcontrol.updateFolderParentuuid(uuid, newParentUuid);
                return uuid;
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        LogUtil.d(TAG, "被修改层级的文件夹uuid：" + s);
                    }
                });
    }


    /**
     * @param info 即将插入数据库的书签信息
     *             将书签信息插入数据库
     */
    public void insertBookmark(WebPage_Info info) {
        Observable.create(new ObservableOnSubscribe<WebPage_Info>() {
            @Override
            public void subscribe(ObservableEmitter<WebPage_Info> emitter) throws Exception {
                sBookmarkDBcontrol.Insert(info);
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<WebPage_Info>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(WebPage_Info webPage_info) {
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
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
    public List<BookmarkFolderNode> getBookmarkFolderList() {
        return bookmarkFolderList;
    }

}
