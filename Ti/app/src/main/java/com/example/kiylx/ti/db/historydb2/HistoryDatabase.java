package com.example.kiylx.ti.db.historydb2;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

/**
 * 创建者 kiylx
 * 创建时间 2020/4/28 22:54
 */
@Database(entities = {HistoryEntity.class},version = 1)
public abstract class HistoryDatabase extends RoomDatabase {
    private static HistoryDatabase database;

    public static HistoryDatabase getInstance(Context context){
        if (database==null){
            synchronized (HistoryDatabase.class){
                if (database==null){
                    database= Room.databaseBuilder(context.getApplicationContext(),HistoryDatabase.class,"history_db").build();
                }
            }
        }
        return database;
    }

    public abstract HistoryDao historyDao();

}
