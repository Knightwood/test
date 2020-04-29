package com.example.kiylx.ti.db.historydb2;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

/**
 * 创建者 kiylx
 * 创建时间 2020/4/28 22:54
 */
@Dao
public interface HistoryDao {
    @Query("SELECT * FROM historys_tab")
    List<HistoryEntity> getAll();

    @Query("SELECT * FROM historys_tab WHERE url LIKE :URL OR title LIKE :URL")
    List<HistoryEntity> getMatchersList(String URL);

    @Insert
    void insert(HistoryEntity entity);

    @Update
    void update(HistoryEntity... entity);

    @Delete
    void delete(HistoryEntity entity);

    @Query("DELETE FROM historys_tab")
    void deleteAll();

    @Query("Delete FROM historys_tab WHERE date=:date")
    void deleteFromDate(String date);
}
