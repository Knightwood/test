package com.example.kiylx.ti.Core1;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.kiylx.ti.Corebase.WebPage_Info;
import com.example.kiylx.ti.INTERFACE.HistoryInterface;
import com.example.kiylx.ti.historydatabase.HistoryBaseHelper;
import com.example.kiylx.ti.historydatabase.HistoryDbSchema.HistoryTable;
import com.example.kiylx.ti.historydatabase.ItemCursorWrapper;

import java.util.ArrayList;

public class AboutHistory implements HistoryInterface {
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

    private ArrayList<WebPage_Info> getHistoryInfos() {
        ArrayList<WebPage_Info> infos = new ArrayList<>();

        ItemCursorWrapper cursor = queryHistory(null, null);
        //查询时参数两个都是null，则会拿到包含所有数据的cursor
        try {
            /*移动到第一个位置，当不是最后的位置时，
            把数据从cursor中取出来，放进arraylist里，移动到下一个位置。
            最后关闭cursor*/
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                infos.add(cursor.getWebPageInfo());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
        return infos;
    }

    public void addToDataBase(WebPage_Info info) {
        ContentValues values = getContentValues(info);
        mDatabase.insert(HistoryTable.NAME, null, values);
    }

    /**
     * @param info webviewpageinfo
     *             要删除的历史记录信息
     */
    private void remove(WebPage_Info info) {
    }

    /**
     * 删除所有历史记录
     */
    private void removeAll() {
    }

    private ArrayList<WebPage_Info> getlistswithDate(String startDate, String endDate) {
        //查询并返回一个时间段内的所有条目
        ArrayList<WebPage_Info> temp = new ArrayList<>();
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

    private ItemCursorWrapper queryHistory(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                HistoryTable.NAME,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                null
        );
        return new ItemCursorWrapper(cursor);
    }/*通过queryHistory查询数据库，返回的是ItemCursorWrapper类型的cursor，遍历cursor，获取需要的数据。*/

    private ItemCursorWrapper queryHistoryfromDate(String startDate, String endDate) {
        Cursor cursor = mDatabase.rawQuery("SELECT * from history_item where date between ? and ? order by date desc", new String[]{startDate, endDate});
        return new ItemCursorWrapper(cursor);
    }

    private static ContentValues getContentValues(WebPage_Info info) {
        ContentValues values = new ContentValues();
        values.put(HistoryTable.Childs.TITLE, info.getTitle());
        values.put(HistoryTable.Childs.URL, info.getUrl());
        values.put(HistoryTable.Childs.DATE, info.getDate());
        return values;
    }


//以下是实现的接口

    /**
     * @return 返回所有历史记录
     */
    @Override
    public ArrayList<WebPage_Info> getDataLists() {
        return getHistoryInfos();
    }

    /**
     * @param startdate 开始日期
     * @param endDate   结束日期
     * @return 返回开始到结束日期的历史记录
     */
    @Override
    public ArrayList<WebPage_Info> getDataLists(String startdate, String endDate) {
        return getlistswithDate(startdate, endDate);
    }

    /**
     * @param info 要添加进lists中的WebPage_Info
     */
    @Override
    public void addData(WebPage_Info info) {
        addToDataBase(info);
    }

    /**
     * @param info 要删除lists中的WebPage_Info
     */
    @Override
    public void delete(WebPage_Info info) {
        remove(info);
    }

    /**
     * 删除所有历史记录
     */
    @Override
    public void deleteAll() {
        removeAll();
    }


}
