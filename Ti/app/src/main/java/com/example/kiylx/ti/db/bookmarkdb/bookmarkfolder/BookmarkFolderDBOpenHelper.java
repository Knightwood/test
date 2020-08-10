package com.example.kiylx.ti.db.bookmarkdb.bookmarkfolder;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.kiylx.ti.conf.SomeRes;

public class BookmarkFolderDBOpenHelper extends SQLiteOpenHelper {
    private static final int VERSION=1;
    private static final String DATABASE_NAME="BookmarkFolderTab";
    public BookmarkFolderDBOpenHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version){
        super(context, name, factory, version);

    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table "+ BookmarkFolderDbSchema.FolderTable.NAME+"("+"_id integer primary key autoincrement, "+
                BookmarkFolderDbSchema.FolderTable.childs.FOLDER +
                BookmarkFolderDbSchema.FolderTable.childs.UUID+
                BookmarkFolderDbSchema.FolderTable.childs.PARENTUUID+")");

        db.execSQL("INSERT into BookmarkFolderTab values(?,?,?)",new String[]{"tooR", SomeRes.defaultBookmarkFolderUUID,SomeRes.defaultBookmarkFolderUUID});
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
