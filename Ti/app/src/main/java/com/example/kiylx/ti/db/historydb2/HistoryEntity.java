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
    private int uid;
    @ColumnInfo
    private String url;
    @ColumnInfo
    private String title;
    @ColumnInfo
    private String date;


    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
