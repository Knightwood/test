package com.example.kiylx.ti.db.bookmarkdb.bookmark;

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
    public WebPage_Info getFavoriterinfo(){
        String id=getString(getColumnIndex(FavoritepageDbSchema.FavoriteTable.childs.ID));
        String title = getString(getColumnIndex(FavoritepageDbSchema.FavoriteTable.childs.TITLE));
        String url = getString(getColumnIndex(FavoritepageDbSchema.FavoriteTable.childs.url));
        String folder = getString(getColumnIndex(FavoritepageDbSchema.FavoriteTable.childs.BookmarkFolderUuid));

        return new WebPage_Info.Builder(url).uuid(id).title(title).bookmarkFolderUUID( folder).build();
        //return new WebPage_Info(id,title,url,folder);
    }

}
