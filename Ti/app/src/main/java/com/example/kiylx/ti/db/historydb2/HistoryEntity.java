package com.example.kiylx.ti.db.historydb2;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * 创建者 kiylx
 * 创建时间 2020/4/28 22:40
 */
@Entity(tableName = "historys_tab")
public class HistoryEntity {

    public HistoryEntity(String title, String url, String date) {
        this.title = title;
        this.url = url;
        this.date = date;
    }

    @PrimaryKey(autoGenerate = true)
    public int uid;
    @ColumnInfo
    public String url;
    @ColumnInfo
    public String title;
    @ColumnInfo
    public String date;
}
