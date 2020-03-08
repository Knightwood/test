package com.example.kiylx.ti.favoritePageDataBase;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.example.kiylx.ti.corebase.WebPage_Info;

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
        String web_tags = getString(getColumnIndex(FavoritepageDbSchema.FavoriteTable.childs.BookmarkFolder));

        return new WebPage_Info(title,url,web_tags,-1,null);
    }

}
