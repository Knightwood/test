package com.example.kiylx.ti.db.bookmarkdb.bookmark;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.kiylx.ti.model.WebPage_Info;

import java.util.ArrayList;
import java.util.List;

public class AboutBookmark {
    private static AboutBookmark sAboutBookmark;
    private static List<WebPage_Info> bookMarklists;
    private SQLiteDatabase mDatabase;
    private Context mContext;
    private String currentPath;//当前展示的所有内容所属的父类的uuid。父向uuid：abc，子项

    private AboutBookmark(Context context) {
        mContext = context;
        bookMarklists = new ArrayList<>();//暂时没有用
        mDatabase = new FavoritePageBaseHelper(mContext).getWritableDatabase();
    }

    public static AboutBookmark get(Context context) {
        if (sAboutBookmark == null) {
            sAboutBookmark = new AboutBookmark(context);
        }
        return sAboutBookmark;
    }

    //==========================以下数据库操作=========================//
    public void InsertItem(WebPage_Info info) {
        if (info == null || info.getUrl() == null) {
            return;
        }
        if (isMarked(info)) {
            return;
        }
        ContentValues values = getContentValues(info);
        mDatabase.insert(FavoritepageDbSchema.FavoriteTable.NAME, null, values);

    }

    private void Insert(ContentValues values) {

    }

    /**
     * @param info webpageinfo
     *             更新数据库中的条目信息
     *             利用uuid，查询到记录，然后更新title和url，bookmarkFolder
     */
    public void updateItem(WebPage_Info info) {
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
     * @param id webpage_info的id
     *           根据uuid删除书签
     */
    public void deleteItem(String id) {
        try {
            mDatabase.execSQL("DELETE from Favorite_tab where uuid =?", new String[]{id});
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void delete(String id) {

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
     * @param folder 书签文件夹的名称
     * @return 返回该标签下的所有书签
     */
    public List<WebPage_Info> getBookmarks(String folder) {
        List<WebPage_Info> mlists = new ArrayList<>();//用来放查找结果
        ItemCursorWrapper cursor;
        if (folder == null) {
            cursor = queryFavority(null, null);
        } else
            cursor = queryFavority(FavoritepageDbSchema.FavoriteTable.childs.BookmarkFolderUuid + " =?", new String[]{folder});
        try {
            if (cursor.getCount() == 0) {
                return mlists;
            }
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                mlists.add(cursor.getFavoriterinfo());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
        return mlists;
    }

    /**
     * @param str 书签文件夹名称
     * @return 返回该文件夹下的书签列表或返回所有书签
     */
    public List<WebPage_Info> getChangeLists(String str) {
        //返回tag的书签list
        if (str.equals("所有书签")) {
            return getAllInfos();
        }
        return getBookmarks(str);
    }

    /**
     * @param oldfolderName 旧的标签名称
     * @param newFolderName 新的标签名称
     *                      根据tag批量更改条目的信息,更新书签记录的tag名称
     */
    public void updateFolderforItems(String oldfolderName, String newFolderName) {
        mDatabase.execSQL("UPDATE Favorite_tab SET folderName = ? where folderName = ?", new String[]{newFolderName, oldfolderName});
    }

    public void deleteBookMarkWithFolderName(String folderName) {
        //根据folderName这个文件夹删除相关的条目
        mDatabase.execSQL("DELETE FROM Favorite_tab where folderName=?", new String[]{folderName});
    }


    /**
     * @return 返回所有的书签记录
     */
    private List<WebPage_Info> getAllInfos() {
        //第一个参数来指示查询哪一列
        List<WebPage_Info> mlists = new ArrayList<>();//用来放查找结果
        ItemCursorWrapper cursor = queryFavority(null, null);
        try {
            if (cursor.getCount() == 0) {
                return mlists;
            }
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                mlists.add(cursor.getFavoriterinfo());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
        return mlists;
    }

    /**
     * @param query 书签名称
     * @return 查询名称，返回相应的数据库信息
     */
    public List<WebPage_Info> queryBookmark(String query) {
        List<WebPage_Info> mlists = new ArrayList<>();//用来放查找结果

        String sqlStr = "SELECT * from Favorite_tab where title LIKE ? or url LIKE ? ";
        Cursor cursor1 = mDatabase.rawQuery(sqlStr, new String[]{"%" + query + "%", "%" + query + "%"});
        ItemCursorWrapper cursor = new ItemCursorWrapper(cursor1);

        try {
            if (cursor.getCount() == 0) {
                return mlists;
            }
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                mlists.add(cursor.getFavoriterinfo());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
        return mlists;
    }

    //========================================================//
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

}