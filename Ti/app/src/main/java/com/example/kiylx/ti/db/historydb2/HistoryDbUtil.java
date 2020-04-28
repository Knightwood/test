package com.example.kiylx.ti.db.historydb2;

import android.content.Context;

/**
 * 创建者 kiylx
 * 创建时间 2020/4/28 22:55
 */
public class HistoryDbUtil {
    public static HistoryDao getDao(Context context){
        return HistoryDatabase.getInstance(context).historyDao();
    }
}
