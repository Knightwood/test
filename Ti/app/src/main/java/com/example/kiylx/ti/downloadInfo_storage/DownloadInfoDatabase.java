package com.example.kiylx.ti.downloadInfo_storage;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {DownloadEntity.class}, version = 1)

public abstract class DownloadInfoDatabase extends RoomDatabase {
    private static DownloadInfoDatabase sDownloadInfoDatabase;

    public static DownloadInfoDatabase getInstance(Context context){
        if (sDownloadInfoDatabase==null){
            synchronized (DownloadInfoDatabase.class){
                if (sDownloadInfoDatabase==null){
                    sDownloadInfoDatabase= Room.databaseBuilder(context.getApplicationContext(),DownloadInfoDatabase.class,"downloadinfo_db").build();
                }
            }
        }
        return sDownloadInfoDatabase;
    }
    public static void destroy(){
        sDownloadInfoDatabase=null;
    }
    //抽象方法getDao()，这个是获取数据库操作接口的方法，不用我们实现，编译通过后，room会帮我们实现
    public abstract DownloadDao downloadDao();
}
