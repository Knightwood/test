package com.example.kiylx.ti.db.searchenginedb;

import android.content.Context;

/**
 * @创建者 kiylx
 * @创建时间 2020/2/22 16:01
 */
public class SearchEngine_db_Util {
    public static SearchEngineDao getDao(Context context){
        return SearchEngineDatabase.getInstance(context).searchEngineDao();
    }
}
