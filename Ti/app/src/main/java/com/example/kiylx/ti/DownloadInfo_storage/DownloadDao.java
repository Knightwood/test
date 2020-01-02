package com.example.kiylx.ti.DownloadInfo_storage;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao

public interface DownloadDao {

    @Query("SELECT * FROM downloadInfo_tab")
    List<DownloadEntity> getAll();

    @Insert
    void insertAll(DownloadEntity... info);

    @Delete
    void delete(DownloadEntity info);

    @Update
    void update();
}
