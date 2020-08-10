package com.example.kiylx.ti.db.bookmarkdb.bookmarkfolder;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.example.kiylx.ti.model.BookmarkFolderNode;

public class BookmarkFolderCursorWrapper extends CursorWrapper {
    /**
     * Creates a cursor wrapper.
     *
     * @param cursor The underlying cursor to wrap.
     */
    public BookmarkFolderCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public String getFolderinfo() {
        return getString(getColumnIndex(BookmarkFolderDbSchema.FolderTable.childs.FOLDER));

    }

    public BookmarkFolderNode getNodeInfo() {
        return new BookmarkFolderNode(
                getString(getColumnIndex(BookmarkFolderDbSchema.FolderTable.childs.FOLDER)),
                getString(getColumnIndex(BookmarkFolderDbSchema.FolderTable.childs.UUID)),
                getString(getColumnIndex(BookmarkFolderDbSchema.FolderTable.childs.PARENTUUID)));

    }
}
