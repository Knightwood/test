package com.example.kiylx.ti.favoritepageDataBase;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.example.kiylx.ti.WebPage_Info;

public class ItemCursorWrapper extends CursorWrapper {
    /**
     * Creates a cursor wrapper.
     *
     * @param cursor The underlying cursor to wrap.
     */
    public ItemCursorWrapper(Cursor cursor) {
        super(cursor);
    }
    public WebPage_Info getFavoriterinfo(){
        String title = getString(getColumnIndex(FavoritepageDbSchema.FavoriteTable.childs.TITLE));
        String url = getString(getColumnIndex(FavoritepageDbSchema.FavoriteTable.childs.url));
        String folders = getString(getColumnIndex(FavoritepageDbSchema.FavoriteTable.childs.FOLDERS));

        WebPage_Info info = new WebPage_Info(title,url,folders,null);
        return info;
    }
}
