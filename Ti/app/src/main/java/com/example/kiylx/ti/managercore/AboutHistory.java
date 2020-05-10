package com.example.kiylx.ti.managercore;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.kiylx.ti.corebase.WebPage_Info;
import com.example.kiylx.ti.tool.dateProcess.TimeProcess;
import com.example.kiylx.ti.trash.historydb.HistoryBaseHelper;
import com.example.kiylx.ti.trash.historydb.HistoryDbSchema.HistoryTable;
import com.example.kiylx.ti.trash.historydb.ItemCursorWrapper;

import java.util.ArrayList;
import java.util.List;

public class AboutHistory {
    private static AboutHistory sAboutHistory;
    private SQLiteDatabase mDatabase;

    private AboutHistory(Context context) {
        mDatabase = new HistoryBaseHelper(context).getWritableDatabase();
    }

    public static AboutHistory get(Context context) {
        if (null == sAboutHistory) {
            sAboutHistory = new AboutHistory(context);
        }
        return sAboutHistory;
    }


    /**
     * @param startDate 开始时间
     * @param endDate   结束时间
     * @return 这段时间内的所有历史记录，如果传入的都是null，返回所有历史记录
     */
    private List<WebPage_Info> getlistswithDate(String startDate, String endDate) {
        //查询并返回一个时间段内的所有条目
        List<WebPage_Info> temp = new ArrayList<>();
        ItemCursorWrapper cursor = queryHistoryfromDate(startDate, endDate);

        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                temp.add(cursor.getWebPageInfo());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
        return temp;

    }

    /**
     * @param info WebPage_Info
     *             加入数据库
     */
    public void addToDataBase(WebPage_Info info) {
        info.setDate(TimeProcess.getTime());//添加上时间，避免时间信息缺失
        ContentValues values = getContentValues(info);
        mDatabase.insert(HistoryTable.NAME, null, values);
    }

    /**
     * @param queryText 要查找的文本
     * @return 返回匹配的历史记录
     * 这是历史记录查询功能
     */
    public List<WebPage_Info> querySomeOne(String queryText) {
        List<WebPage_Info> mhistoryList = new ArrayList<>();
        String sqlstr = "SELECT * from history_item where title LIKE ? or url LIKE ?";
        Cursor cursor = mDatabase.rawQuery(sqlstr, new String[]{"%" + queryText + "%", "%" + queryText + "%"});
        ItemCursorWrapper cursorWrapper = new ItemCursorWrapper(cursor);
            try {
                if (cursorWrapper.getCount() == 0){
                    return mhistoryList;
                }
                cursorWrapper.moveToFirst();
                while (!cursorWrapper.isAfterLast()) {
                    mhistoryList.add(cursorWrapper.getWebPageInfo());
                    cursorWrapper.moveToNext();
                }
            } finally {
                cursorWrapper.close();
            }
        return mhistoryList;
    }

    private ItemCursorWrapper queryHistoryfromDate(String startDate, String endDate) {
        Cursor cursor;
        if (startDate == null) {
            cursor = mDatabase.rawQuery("SELECT * from histoy_item", null);
        } else
            cursor = mDatabase.rawQuery("SELECT * from history_item where date between ? and ? order by date desc", new String[]{startDate, endDate});

        return new ItemCursorWrapper(cursor);
    }

    private static ContentValues getContentValues(WebPage_Info info) {
        ContentValues values = new ContentValues();
        values.put(HistoryTable.Childs.TITLE, info.getTitle());
        values.put(HistoryTable.Childs.URL, info.getUrl());
        values.put(HistoryTable.Childs.DATE, info.getDate());
        return values;
    }


    /**
     * @return 返回所有历史记录
     */

    public List<WebPage_Info> getDataLists() {
        return getlistswithDate(null, null);
    }

    /**
     * @param startdate 开始日期
     * @param endDate   结束日期
     * @return 返回开始到结束日期的历史记录
     */

    public List<WebPage_Info> getDataLists(String startdate, String endDate) {
        return getlistswithDate(startdate, endDate);
    }

    /**
     * @param url 要删除的历史记录的网址
     */
    public void delete(String url) {
        mDatabase.execSQL("DELETE FROM history_item where url =?", new String[]{url});

    }

    /**
     * 删除所有历史记录
     */

    public void deleteAll() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                mDatabase.execSQL("DELETE FROM history_item");
            }
        }).start();

    }

    public void updateTitle(WebPage_Info info) {
        mDatabase.execSQL("UPDATE history_item set title=? where url=?", new String[]{info.getTitle(), info.getUrl()});
    }
}
