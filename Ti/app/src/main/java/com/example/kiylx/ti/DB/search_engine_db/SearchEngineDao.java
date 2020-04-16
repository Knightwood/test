package com.example.kiylx.ti.DB.search_engine_db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

/**
 * @创建者 kiylx
 * @创建时间 2020/2/22 15:36
 */
@Dao
public interface SearchEngineDao {
    @Query("SELECT * FROM searchEngine_tab")
    List<SearchEngineEntity> getAll();
    @Query("SELECT * FROM searchEngine_tab WHERE check_b=:b1")
    List<SearchEngineEntity> getItem(Boolean b1);

    @Insert
    void insert(SearchEngineEntity... entity);

    @Delete
    void delete(SearchEngineEntity entity);

    @Update
    void update(SearchEngineEntity entity);

    @Query("DELETE FROM searchEngine_tab WHERE url=:url1")
    void deleteitem(String url1);

    @Query("UPDATE searchEngine_tab SET check_b =:b WHERE url=:engine")
    void updateBooleaan(String engine, boolean b);

    /**
     * @param url1 旧字符串（待更新字符串）
     * @param url2 新字符串
     */
    @Query("UPDATE searchEngine_tab SET url=:url2 WHERE url=:url1")
    void updateURL(String url1, String url2);

}
