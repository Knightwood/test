package com.example.kiylx.ti.trash.historydb;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.kiylx.ti.trash.historydb.HistoryDbSchema.HistoryTable;

public class HistoryBaseHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "history.db";

    public HistoryBaseHelper(Context context) {
        super(context,DATABASE_NAME,null,VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table "+ HistoryTable.NAME+"("+"_id integer primary key autoincrement, "+ HistoryTable.Childs.TITLE +","+ HistoryTable.Childs.URL +","+ HistoryTable.Childs.DATE +")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
