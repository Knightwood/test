package com.example.kiylx.ti.DB.search_engine_db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

/**
 * @创建者 kiylx
 * @创建时间 2020/2/22 15:40
 */
@Database(entities = {SearchEngineEntity.class}, version = 1,exportSchema = false)
public abstract class SearchEngineDatabase extends RoomDatabase {
    private static SearchEngineDatabase database;

    public static SearchEngineDatabase getInstance(Context context){
        if (database==null){
            synchronized (SearchEngineDatabase.class){
                if (database==null){
                    database= Room.databaseBuilder(context.getApplicationContext(),SearchEngineDatabase.class,"SearchEngine_db").build();
                }
            }
        }
        return database;
    }
    public static void destroy(){
        database=null;
    }
    public abstract SearchEngineDao searchEngineDao();
}
