package com.example.kiylx.ti.HistoryDataBase;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.example.kiylx.ti.Corebase.WebPage_Info;

public class ItemCursorWrapper extends CursorWrapper {
    /**
     * Creates a cursor wrapper.
     *
     * @param cursor The underlying cursor to wrap.
     */
    public ItemCursorWrapper(Cursor cursor) {
        super(cursor);
    }
    public WebPage_Info getWebPageInfo(){
        String url=getString(getColumnIndex(HistoryDbSchema.HistoryTable.Childs.URL));
        String title=getString(getColumnIndex(HistoryDbSchema.HistoryTable.Childs.TITLE));
        String date=getString(getColumnIndex(HistoryDbSchema.HistoryTable.Childs.DATE));

        return new WebPage_Info(title,url,null,1,date);

    }
}
