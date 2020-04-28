package com.example.kiylx.ti.db.historydb2;

import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.RawQuery;

import java.util.List;

/**
 * 创建者 kiylx
 * 创建时间 2020/4/28 22:54
 */
public interface HistoryDao {
    @Query("SELECT * FROM historys_tab")
    List<HistoryEntity> getAll();

    @Query("SELECT * FROM historys_tab WHERE url LIKE :URL OR title LIKE :URL")
    List<HistoryEntity> getMatchersList(String URL);

    @Insert
    void insert(HistoryEntity entity);

}
