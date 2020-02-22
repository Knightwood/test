package com.example.kiylx.ti.search_engine_db;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * @创建者 kiylx
 * @创建时间 2020/2/22 15:24
 */
@Entity(tableName = "searchEngine_tab")
public class SearchEngineEntity {
    @PrimaryKey
    @NonNull
    private String url;
    @ColumnInfo
    private boolean check;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isCheck() {
        return check;
    }

    public void setCheck(boolean check) {
        this.check = check;
    }
}
