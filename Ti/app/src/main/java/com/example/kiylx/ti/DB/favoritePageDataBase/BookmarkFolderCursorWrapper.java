package com.example.kiylx.ti.DB.favoritePageDataBase;

import android.database.Cursor;
import android.database.CursorWrapper;

public class BookmarkFolderCursorWrapper extends CursorWrapper {
    /**
     * Creates a cursor wrapper.
     *
     * @param cursor The underlying cursor to wrap.
     */
    public BookmarkFolderCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public String getFolderinfo(){
        return getString(getColumnIndex(BookmarkFolderDbSchema.FolderTable.childs.FOLDER));

    }
}
