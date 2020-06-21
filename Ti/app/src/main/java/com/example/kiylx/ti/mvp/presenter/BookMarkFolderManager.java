package com.example.kiylx.ti.mvp.presenter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.kiylx.ti.conf.SomeRes;
import com.example.kiylx.ti.db.bookmarkdb.BookmarkFolderCursorWrapper;
import com.example.kiylx.ti.db.bookmarkdb.BookmarkFolderDbSchema;
import com.example.kiylx.ti.db.bookmarkdb.BookmarkFolderDBOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class BookMarkFolderManager {
    /*书签文件夹，如果在添加标签的时候选择的是未分类，那么这一条*/
    private static BookMarkFolderManager sBookMarkFolderManager;
    private SQLiteDatabase mDatabase;
    private List<String> bookmarkFolderlists;
    /*taglist是一个全局的列表，
    会从数据库里拿数据后添加进taglist返回给需要的对象，
    所以，必要时需要判断taglist是不是空的再决定是否从数据库里拿数据放进去*/
    /*添加或是删除时，会让数据库和taglists保持一致，因为其他类的lists也是taglist的引用，所以不需要做额外工作*/

    private BookMarkFolderManager(Context context) {

        mDatabase = new BookmarkFolderDBOpenHelper(context, BookmarkFolderDbSchema.FolderTable.NAME, null, 1).getWritableDatabase();
        bookmarkFolderlists = new ArrayList<>();
        bookmarkFolderlists.add(0, SomeRes.defaultBookmarkFolder);//在这里添加了未分类文件夹，数据库里是没有的
    }

    public static BookMarkFolderManager get(Context context) {
        if (null == sBookMarkFolderManager) {
            sBookMarkFolderManager = new BookMarkFolderManager(context);
        }
        return sBookMarkFolderManager;
    }

    /**
     * 文件夹名称不是“未分类”就添加进去
     */
    private void addTagintoLists(String str) {
        if (!str.equals("未分类"))
            bookmarkFolderlists.add(str);
    }

    private void deleteTagfromLists(String str) {
        if (!str.equals("未分类"))
            bookmarkFolderlists.remove(str);
    }

    /**
     * @param str “标签”名称
     * @return 返回“标签”在taglist中的位置
     * 以此来设置spinner显示特定项
     * 如果网页信息中tag是空的，那就返回0，显示成：“未分类”。
     */
    public int getPosfromLists(String str) {
        if (str == null) {
            return 0;
        }
        return bookmarkFolderlists.indexOf(str);
    }

    /**
     * @param pos 位置
     * @return 返回存储“标签”的list中pos这个位置存储的“标签”
     */
    public String getFolderfromList(int pos) {
        return bookmarkFolderlists.get(pos);
    }

    /**
     * @return 获取的taglist的大小
     */
    public int getSize() {
        return bookmarkFolderlists.size();
    }

    /**
     * @param folderName 要查询的“文件夹”名称
     * @return 如果要查询的“文件夹”存在，返回真
     * 查询是否有与文件夹重复的元素
     */
    private boolean isExist(String folderName) {

        return bookmarkFolderlists.contains(folderName);
    }

    /**
     * @return 第一次初始化的文件夹列表
     * 在其他的类里第一次获取列表所调用
     */
    public List<String> getBookmarkFolderlists() {
        if (bookmarkFolderlists == null) {
            bookmarkFolderlists = new ArrayList<>();
        }
        //列表中只有一个“未分类”,那就获取数据库中的数据，否则就先清空列表再获取数据
        if (!bookmarkFolderlists.isEmpty()) {
            bookmarkFolderlists.clear();
        }
        bookmarkFolderlists.add(SomeRes.defaultBookmarkFolder);
        bookmarkFolderlists = getfolderListfromDB();
        return bookmarkFolderlists;
    }

    //=============================以下数据库操作===================//
    public void add(String folder) {
        if (isExist(folder)) {
            return;
        }
        Log.d("tag添加", folder);
        ContentValues values = getContentValues(folder);
        mDatabase.insert(BookmarkFolderDbSchema.FolderTable.NAME, null, values);
        addTagintoLists(folder);

    }

    public void delete(String folderName) {
        mDatabase.delete(BookmarkFolderDbSchema.FolderTable.NAME, BookmarkFolderDbSchema.FolderTable.childs.FOLDER + " =? ", new String[]{folderName});
        deleteTagfromLists(folderName);

    }

    /**
     * @param oldName 文件夹旧有名称
     * @param newName 文件夹新名称
     */
    public void updateitem(String oldName, String newName) {
        mDatabase.execSQL("UPDATE BookmarkFolderTab SET folderName=? where folderName=?", new String[]{newName, oldName});
        //updateFolderName(oldName, newName);//实时刷新taglist中的值
        //刷新列表
        bookmarkFolderlists = getBookmarkFolderlists();
    }


    private static ContentValues getContentValues(String folderName) {
        ContentValues values = new ContentValues();
        values.put(BookmarkFolderDbSchema.FolderTable.childs.FOLDER, folderName);

        return values;
    }

    /**
     * @return 返回书签文件夹列表
     * 默认的“未分类”文件夹并不添加进数据库
     */
    private List<String> getfolderListfromDB() {
        BookmarkFolderCursorWrapper cursor = getAllFolder(null);
        //如果文件夹list仅有一个“未分类，那有两种情况，一种是数据库里没有其他标签，一种是文件夹list仅被初始化还没有从数据库里拿数据
        if (!bookmarkFolderlists.isEmpty() )
            try {
                Log.d("书签文件夹数量", String.valueOf(cursor.getCount()));
                if (cursor.getCount() == 0) {
                    return bookmarkFolderlists;
                }
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    bookmarkFolderlists.add(cursor.getFolderinfo());
                    cursor.moveToNext();
                }
            } finally {
                cursor.close();
            }
        return bookmarkFolderlists;

    }

    private BookmarkFolderCursorWrapper queryFolder(String where, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                BookmarkFolderDbSchema.FolderTable.NAME,
                null,
                where,
                whereArgs,
                null,
                null,
                null);
        return new BookmarkFolderCursorWrapper(cursor);
    }

    private BookmarkFolderCursorWrapper getAllFolder(String name) {
        Cursor cursor;
        if (name == null) {
            cursor = mDatabase.rawQuery("SELECT * from BookmarkFolderTab", null);
        } else {
            cursor = mDatabase.rawQuery("SELECT * from BookmarkFolderTab where folderName = ?", new String[]{name});
        }

        return new BookmarkFolderCursorWrapper(cursor);
    }
}
