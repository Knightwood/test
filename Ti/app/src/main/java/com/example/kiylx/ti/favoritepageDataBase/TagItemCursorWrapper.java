package com.example.kiylx.ti.favoritepageDataBase;

import android.database.Cursor;
import android.database.CursorWrapper;

public class TagItemCursorWrapper extends CursorWrapper {
    /**
     * Creates a cursor wrapper.
     *
     * @param cursor The underlying cursor to wrap.
     */
    public TagItemCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public String getTaginfo(){
        return getString(getColumnIndex(TagDbSchema.TagTable.childs.TAG));

    }
}
