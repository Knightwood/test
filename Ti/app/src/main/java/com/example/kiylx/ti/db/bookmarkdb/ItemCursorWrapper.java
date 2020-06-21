package com.example.kiylx.ti.db.bookmarkdb;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.example.kiylx.ti.mvp.model.WebPage_Info;

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
        String id=getString(getColumnIndex(FavoritepageDbSchema.FavoriteTable.childs.ID));
        String title = getString(getColumnIndex(FavoritepageDbSchema.FavoriteTable.childs.TITLE));
        String url = getString(getColumnIndex(FavoritepageDbSchema.FavoriteTable.childs.url));
        String folder = getString(getColumnIndex(FavoritepageDbSchema.FavoriteTable.childs.BookmarkFolder));

        return new WebPage_Info(id,title,url,folder);
    }

}
