package com.example.kiylx.ti.FavoritePageDataBase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.kiylx.ti.FavoritePageDataBase.FavoritepageDbSchema.FavoriteTable;

public class FavoritePageBaseHelper extends SQLiteOpenHelper {

    private static final int VERSION=1;
    private static final String DATABASE_NAME="favorite_tab";

    public FavoritePageBaseHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table "+ FavoriteTable.NAME+"("+"_id integer primary key autoincrement, "+FavoriteTable.childs.TITLE+","+FavoriteTable.childs.url+","+FavoriteTable.childs.TAG +")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
