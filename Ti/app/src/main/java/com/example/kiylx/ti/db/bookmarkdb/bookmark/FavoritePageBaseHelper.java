package com.example.kiylx.ti.db.bookmarkdb.bookmark;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.kiylx.ti.db.bookmarkdb.bookmark.FavoritepageDbSchema.FavoriteTable;

public class FavoritePageBaseHelper extends SQLiteOpenHelper {

    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "favorite.db";

    public FavoritePageBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + FavoriteTable.NAME + "(" + "_id integer primary key autoincrement, " + FavoriteTable.childs.ID + "," + FavoriteTable.childs.TITLE + "," + FavoriteTable.childs.url + "," + FavoriteTable.childs.BookmarkFolderUuid + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
