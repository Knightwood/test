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

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
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

    private static volatile BookmarkManager sBookmarkManager;

    private String currentFolderUUID;//当前展示的所有内容所属的父类的uuid。

    private List<WebPage_Info> bookmarkList;
    private List<BookmarkFolderNode> bookmarkFolderList;

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
        getIndex(SomeRes.defaultBookmarkFolderUUID);
    }

    /**
     * @param uuid 当前文件夹的uuid
     *             从数据库读取文件夹和书签记录，放在同一个list中，并更新界面
     */
    public void getIndex(@NotNull String uuid) {
        Observable observable1 = Observable.create(new ObservableOnSubscribe<List<BookmarkFolderNode>>() {
            @Override
            public void subscribe(ObservableEmitter<List<BookmarkFolderNode>> emitter) throws Exception {
                emitter.onNext(sBookmarkfolderDBcontrol.queryFolder(uuid, true));
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io());

        Observable observable2 = Observable.create(new ObservableOnSubscribe<List<WebPage_Info>>() {
            @Override
            public void subscribe(ObservableEmitter<List<WebPage_Info>> emitter) throws Exception {
                emitter.onNext(sBookmarkDBcontrol.queryBookmarks(uuid));
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io());


        Observable.zip(observable1, observable2, new BiFunction<List<BookmarkFolderNode>, List<WebPage_Info>, List<WebPage_Info>>() {
            @Override
            public List<WebPage_Info> apply(List<BookmarkFolderNode> bookmarkFolderNodes, List<WebPage_Info> webPage_infos) throws Exception {
                List<WebPage_Info> result = new ArrayList<>();
                result.addAll(bookmarkFolderNodes);
                result.addAll(webPage_infos);
                return result;
            }
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<WebPage_Info>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(List<WebPage_Info> bookmarkFolderNodeWebPage_infokeyValue) {
                        viewContract.UpdateUI(bookmarkFolderNodeWebPage_infokeyValue);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });


        /*ThreadUtil.MethodThread thread = new ThreadUtil.MethodThread(objs -> {
            query((String) objs[0], (List<BookmarkFolderNode>) objs[1], (List<WebPage_Info>) objs[2]);
        }, UUID, folder, bookmarks);
        Thread thread1 = new Thread(thread);
        thread1.start();*/
    }


    /**
     * @param name       将要创建的文件夹名称
     * @param parentUUID 文件夹所属的父级文件夹
     * @return 返回创建并插入数据库的文件夹node
     */
    public BookmarkFolderNode createFolder(@NotNull String name,@NotNull  String parentUUID) {
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
        Observable.just(1)
                .observeOn(Schedulers.io())
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        if (integer == 1) {
                            sBookmarkfolderDBcontrol.deleteFolder(uuid);
                            if (deleteBookmark)
                                sBookmarkDBcontrol.delete(uuid, true);
                            else
                                sBookmarkDBcontrol.updateFolderUUID(uuid, SomeRes.defaultBookmarkFolderUUID);
                        }
                    }
                });
    }

    public void changeFolderName(@NotNull String uuid,@NotNull String newName) {
        Observable.just(1)
                .observeOn(Schedulers.io())
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        if (integer == 1) {
                            sBookmarkfolderDBcontrol.updateFolderName(uuid, newName);
                        }
                    }
                });
    }

    /**
     * @param uuid          文件夹本身的uuid
     * @param newParentUuid 将文件夹迁移到另一个的文件夹下，这是另一个文件夹的uuid
     */
    public void changeFolderLevel(@NotNull String uuid, @NotNull String newParentUuid) {
        if (uuid.equals(newParentUuid))
            return;
        Observable.just(1)
                .observeOn(Schedulers.io())
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        if (integer == 1) {
                            sBookmarkfolderDBcontrol.updateFolderParentuuid(uuid, newParentUuid);
                        }
                    }
                });
    }


}
