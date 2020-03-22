package com.example.kiylx.ti.downloadInfo_storage;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao

public interface DownloadDao {

    @Query("SELECT * FROM downloadInfo_tab")
    LiveData<List<DownloadEntity>>  getAll();

    @Query("SELECT * FROM downloadInfo_tab WHERE url =:URL")
    List<DownloadEntity> getOne(String URL);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAll(DownloadEntity... infos);
/*
//onConflict:冲突
 OnConflictStrategy.ABORT (default) to roll back the transaction on conflict.
 使用 OnConflictStrategy.REPLACE 以用新的行替换旧的行.
 使用 OnConflictStrategy.IGNORE什么也不做.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert1(DownloadEntity... infos);
    */

    @Delete
    void delete(DownloadEntity info);

    @Update
    void update(DownloadEntity... infos);
}
