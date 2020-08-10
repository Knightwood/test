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
import com.example.kiylx.ti.tool.KeyValue;

import org.jetbrains.annotations.NotNull;

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
public class BookmarkControl extends BasePresenter<BookmarkActivityContract> {

    private static volatile BookmarkControl sBookmarkControl;

    private String currentFolderUUID;//当前展示的所有内容所属的父类的uuid。

    private List<WebPage_Info> bookmarkList;
    private List<BookmarkFolderNode> bookmarkFolderList;

    private BookmarkDBcontrol sBookmarkDBcontrol;
    private BookmarkfolderDBcontrol sBookmarkfolderDBcontrol;

    public static BookmarkControl getInstance(Context context, BaseContract.View view) {
        if (sBookmarkControl == null) {
            synchronized (BookmarkControl.class) {
                if (sBookmarkControl == null) {
                    sBookmarkControl = new BookmarkControl(context, view);
                }
            }
        }
        return sBookmarkControl;
    }

    private BookmarkControl(Context context, BaseContract.View view) {
        super((BookmarkActivityContract) view);
        sBookmarkDBcontrol = BookmarkDBcontrol.get(context);
        sBookmarkfolderDBcontrol = BookmarkfolderDBcontrol.get(context);
        getIndex(SomeRes.defaultBookmarkFolderUUID);
    }

    public void getIndex(String uuid) {
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


        Observable.zip(observable1, observable2, new BiFunction<List<BookmarkFolderNode>, List<WebPage_Info>, KeyValue<BookmarkFolderNode, WebPage_Info>>() {
            @Override
            public KeyValue<BookmarkFolderNode, WebPage_Info> apply(List<BookmarkFolderNode> bookmarkFolderNodes, List<WebPage_Info> webPage_infos) throws Exception {
                return new KeyValue<>(bookmarkFolderNodes, webPage_infos);
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<KeyValue<BookmarkFolderNode, WebPage_Info>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(KeyValue<BookmarkFolderNode, WebPage_Info> bookmarkFolderNodeWebPage_infokeyValue) {
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


    public BookmarkFolderNode createFolder(String name, String parentUUID) {
        final BookmarkFolderNode[] node = {null};
        Observable.fromCallable(new Callable<BookmarkFolderNode>() {

            @Override
            public BookmarkFolderNode call() throws Exception {
                return sBookmarkfolderDBcontrol.insertNode(new BookmarkFolderNode(name, UUID.randomUUID().toString(), parentUUID));
            }
        }).subscribeOn(Schedulers.io())
                .subscribe(new Consumer<BookmarkFolderNode>() {
                    @Override
                    public void accept(BookmarkFolderNode bookmarkFolderNode) throws Exception {
                        node[0] = bookmarkFolderNode;
                    }
                });
        return node[0];

    }


}
