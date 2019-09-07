package com.example.kiylx.ti.historydatabase;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.example.kiylx.ti.model.WebPage_Info;

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
        String url=getString(getColumnIndex(HistoryDbSchema.HistoryTable.Entry.URL));
        String title=getString(getColumnIndex(HistoryDbSchema.HistoryTable.Entry.TITLE));
        String date=getString(getColumnIndex(HistoryDbSchema.HistoryTable.Entry.DATE));

        WebPage_Info minfo=new WebPage_Info(title,url,date);
        return minfo;

    }
}
