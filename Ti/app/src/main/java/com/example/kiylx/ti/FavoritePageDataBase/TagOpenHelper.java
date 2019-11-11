package com.example.kiylx.ti.FavoritePageDataBase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class TagOpenHelper extends SQLiteOpenHelper {
    private static final int VERSION=1;
    private static final String DATABASE_NAME="TagTab";
    public TagOpenHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version){
        super(context, name, factory, version);

    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table "+ TagDbSchema.TagTable.NAME+"("+"_id integer primary key autoincrement, "+ TagDbSchema.TagTable.childs.TAG+")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
