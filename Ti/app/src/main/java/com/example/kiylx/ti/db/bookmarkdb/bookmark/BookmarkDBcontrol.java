package com.example.kiylx.ti.db.bookmarkdb.bookmark;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.kiylx.ti.model.WebPage_Info;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * 创建者 kiylx
 * 创建时间 2020/8/8 22:48
 * packageName：com.example.kiylx.ti.db.bookmarkdb.bookmark
 * 描述：
 */
public class BookmarkDBcontrol {
    private static BookmarkDBcontrol bookmarkDBcontrol;
    private SQLiteDatabase mDatabase;


    public static BookmarkDBcontrol get(Context context) {
        if (bookmarkDBcontrol == null) {
            bookmarkDBcontrol = new BookmarkDBcontrol(context);
        }
        return bookmarkDBcontrol;
    }

    private BookmarkDBcontrol(Context context) {
        mDatabase = new FavoritePageBaseHelper(context).getWritableDatabase();
    }

    /**
     * @param info 即将插入数据库的书签信息
     *             将书签信息插入数据库
     */
    public void insertBookmark(WebPage_Info info) {
        Observable.create(new ObservableOnSubscribe<WebPage_Info>() {
            @Override
            public void subscribe(ObservableEmitter<WebPage_Info> emitter) throws Exception {
                Insert(info);
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


    public void Insert(WebPage_Info info) {
        if (info == null || info.getUrl() == null) {
            return;
        }
        if (isMarked(info)) {
            return;
        }
        ContentValues values = getContentValues(info);
        mDatabase.insert(FavoritepageDbSchema.FavoriteTable.NAME, null, values);

    }

    /**
     * @param info webpageinfo
     *             更新数据库中的条目信息
     *             利用uuid，查询到记录，然后更新title和url，bookmarkFolder
     */
    public void update(WebPage_Info info) {
        String uuid = info.getUuid();
        if (uuid == null) {
            return;
        }
        ContentValues values = new ContentValues();
        values.put(FavoritepageDbSchema.FavoriteTable.childs.TITLE, info.getTitle());
        values.put(FavoritepageDbSchema.FavoriteTable.childs.url, info.getUrl());
        values.put(FavoritepageDbSchema.FavoriteTable.childs.BookmarkFolderUuid, info.getBookmarkFolderUUID());
        try {
            mDatabase.update(FavoritepageDbSchema.FavoriteTable.NAME, values, FavoritepageDbSchema.FavoriteTable.childs.ID + "=?", new String[]{uuid});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param uuid            webpage_info的uuid或是它的partentUUID，这取决于第二个参数。
     * @param isDeleteChild 若是传入true，则将传入的uuid视作某一个文件夹的uuid，将会查询此uuid代表的文件夹下的所有书签，并将其删除
     *                      根据提供的euuid删除书签
     */
    public void delete(String uuid, boolean isDeleteChild) {
        try {
            if (isDeleteChild) {
                mDatabase.execSQL("DELETE from Favorite_tab where folderUUID =?", new String[]{uuid});
            } else {
                mDatabase.execSQL("DELETE from Favorite_tab where uuid =?", new String[]{uuid});
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * @param info webpageinfo
     * @return 这个网址被收藏了返回true
     */
    public boolean isMarked(WebPage_Info info) {
        //判断标准是网址，与数据库里网址一致即为收藏了
        String url = info.getUrl();
        ItemCursorWrapper cursor = queryFavority(FavoritepageDbSchema.FavoriteTable.childs.url + " =?", new String[]{url});
        try {
            //如果查询得到的结果是0个，那就返回flase，表示这个网页还没有被收藏
            return cursor.getCount() != 0;
        } finally {
            cursor.close();
        }
    }

    /**
     * @param folderUUID 书签文件夹的UUID，若传入null，则返回所有的书签记录
     * @return 返回该文件夹的uuid下的所有书签
     */
    public List<WebPage_Info> queryBookmarks(String folderUUID) {
        List<WebPage_Info> mlists = new ArrayList<>();//用来放查找结果
        ItemCursorWrapper cursor;
        if (folderUUID == null) {
            cursor = queryFavority(null, null);
        } else
            cursor = queryFavority(FavoritepageDbSchema.FavoriteTable.childs.BookmarkFolderUuid + " =?", new String[]{folderUUID});
        try {
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    mlists.add(cursor.getFavoriterinfo());
                    cursor.moveToNext();
                }
            }
        } finally {
            cursor.close();
            //closeDB();
        }
        return mlists;
    }


    /**
     * @param oldfolderUUID 原属书签文件夹的uuid
     * @param newFolderUUID 新所属文件夹的uuid
     *                      修改特定文件夹下所有书签，让这些书签属于另一个文件夹
     */
    public void updateFolderUUID(String oldfolderUUID, String newFolderUUID) {
        mDatabase.execSQL("UPDATE Favorite_tab SET folderUUID = ? where folderUUID = ?", new String[]{newFolderUUID, oldfolderUUID});
    }


    /**
     * 模糊查询书签
     *
     * @param query 书签名称
     * @return 查询名称，返回相应的数据库信息
     */
    public List<WebPage_Info> fuzzyQueryBookmark(String query) {
        List<WebPage_Info> mlists = new ArrayList<>();//用来放查找结果

        String sqlStr = "SELECT * from Favorite_tab where title LIKE ? or url LIKE ? ";
        Cursor tmpCursor = mDatabase.rawQuery(sqlStr, new String[]{"%" + query + "%", "%" + query + "%"});
        ItemCursorWrapper cursor = new ItemCursorWrapper(tmpCursor);

        try {
            if ( cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    mlists.add(cursor.getFavoriterinfo());
                    cursor.moveToNext();
                }
            }
        } finally {
            cursor.close();
        }
        return mlists;
    }


    private static ContentValues getContentValues(WebPage_Info info) {
        //存网页信息
        ContentValues values = new ContentValues();
        values.put(FavoritepageDbSchema.FavoriteTable.childs.ID, info.getUuid());
        values.put(FavoritepageDbSchema.FavoriteTable.childs.TITLE, info.getTitle());
        values.put(FavoritepageDbSchema.FavoriteTable.childs.url, info.getUrl());
        values.put(FavoritepageDbSchema.FavoriteTable.childs.BookmarkFolderUuid, info.getBookmarkFolderUUID());

        return values;
    }

    private ItemCursorWrapper queryFavority(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                FavoritepageDbSchema.FavoriteTable.NAME,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                null
        );
        return new ItemCursorWrapper(cursor);
    }

    private void closeDB() {
        if (mDatabase != null) {
            mDatabase.close();
        }
    }
}
