package com.example.kiylx.ti.db.historydb2;

import androidx.paging.DataSource;
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
    @Query("SELECT * FROM historys_tab ORDER BY uid")
    DataSource.Factory<Integer, HistoryEntity> getAll();

    @Query("SELECT * FROM historys_tab WHERE url LIKE :URL OR title LIKE :URL ORDER BY uid")
    DataSource.Factory<Integer, HistoryEntity> getMatchersList(String URL);

    @Query("SELECT * FROM historys_tab ORDER BY uid")
    List<HistoryEntity> getList();

    @Query("SELECT * FROM historys_tab WHERE url LIKE :URL OR title LIKE :URL ORDER BY uid")
    List<HistoryEntity> getMatchersList2(String URL);

    @Insert
    void insert(HistoryEntity entity);

    @Update
    void update(HistoryEntity... entity);

    @Query("UPDATE historys_tab set title=:Title WHERE url=:Url ")
    void updateTitle(String Title, String Url);

    @Delete
    void delete(HistoryEntity entity);

    @Query("DELETE FROM historys_tab")
    void deleteAll();

    @Query("Delete FROM historys_tab WHERE date=:date")
    void deleteFromDate(String date);

    @Query("DELETE FROM historys_tab WHERE url=:Url")
    void deleteWithUrl(String Url);
}
