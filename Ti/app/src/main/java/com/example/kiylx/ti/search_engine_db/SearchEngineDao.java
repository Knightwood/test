package com.example.kiylx.ti.search_engine_db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

/**
 * @创建者 kiylx
 * @创建时间 2020/2/22 15:36
 */
@Dao
public interface SearchEngineDao {
    @Query("SELECT * FROM searchEngine_tab")
    List<SearchEngineEntity> getAll();
    @Insert
    void insert(SearchEngineEntity... entity);
    @Delete
    void delete(SearchEngineEntity entity);

}
