package com.example.kiylx.ti.db.searchenginedb;

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
    private boolean check_b;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isCheck_b() {
        return check_b;
    }

    public void setCheck_b(boolean ischeck) {
        this.check_b = ischeck;
    }
}
