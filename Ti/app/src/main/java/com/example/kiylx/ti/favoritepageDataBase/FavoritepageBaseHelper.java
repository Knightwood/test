package com.example.kiylx.ti.favoritepageDataBase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.kiylx.ti.favoritepageDataBase.FavoritepageDbSchema.FavoriteTable;

public class FavoritepageBaseHelper extends SQLiteOpenHelper {

    private static final int VERSION=1;
    private static final String DATABASE_NAME="favorite_tab";
    public FavoritepageBaseHelper(Context context) {
        super(context,FavoriteTable.NAME,null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table "+ FavoriteTable.NAME+"("+"_id integer primary key autoincrement, "+FavoriteTable.childs.TITLE+","+FavoriteTable.childs.url+","+FavoriteTable.childs.FOLDERS+")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
