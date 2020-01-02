package com.example.kiylx.ti.DownloadInfo_storage;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {DownloadEntity.class},version = 1)
public abstract class downloadInfoDatabase extends RoomDatabase {
    public abstract DownloadDao downloadDao();
}
