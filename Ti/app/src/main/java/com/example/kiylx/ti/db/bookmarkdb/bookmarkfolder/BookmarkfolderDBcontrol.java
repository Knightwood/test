package com.example.kiylx.ti.db.bookmarkdb.bookmarkfolder;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.kiylx.ti.model.BookmarkFolderNode;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 创建者 kiylx
 * 创建时间 2020/8/8 22:45
 * packageName：com.example.kiylx.ti.db.bookmarkdb.bookmarkfolder
 * 描述：
 */
public class BookmarkfolderDBcontrol {
    private SQLiteDatabase mDatabase;
    private static BookmarkfolderDBcontrol bookmarkfolderDBcontrol;

    public static BookmarkfolderDBcontrol get(Context context) {
        if (null == bookmarkfolderDBcontrol) {
            bookmarkfolderDBcontrol = new BookmarkfolderDBcontrol(context);
        }
        return bookmarkfolderDBcontrol;
    }

    private BookmarkfolderDBcontrol(Context context) {
        mDatabase = new BookmarkFolderDBOpenHelper(context, null, 1)
                .getWritableDatabase();
    }

    public void newFolder(String parentUuid, String childName) {
        String childUUID = UUID.randomUUID().toString();
        BookmarkFolderNode node = new BookmarkFolderNode(childName, childUUID, parentUuid);
        insertNode(node);

    }

    public BookmarkFolderNode insertNode(BookmarkFolderNode node) {
        ContentValues values = getContentValues(node);
        mDatabase.insert(BookmarkFolderDbSchema.FolderTable.NAME, null, values);
        return node;
    }

    /**
     * node： uuid和parentUuid
     *
     * @param uuid        文件夹的uuid
     * @param isFindChild 若是查找此节点（文件夹）的所以子节点(传入true)，那就得传入此节点的parentUuid，查询以parentUuid作为parentuuid的值
     * @return 如果存在，返回list，若是不存在，返回空的list
     */
    public List<BookmarkFolderNode> queryFolder(@NotNull String uuid, boolean isFindChild) {
        Cursor tmpCursor = null;
        BookmarkFolderCursorWrapper cursor = null;
        List<BookmarkFolderNode> nodes = new ArrayList<>();
        try {
            if (isFindChild)
                tmpCursor = mDatabase.rawQuery("SELECT * from BookmarkFolderTab where parentUuid =?", new String[]{uuid});
            else
                tmpCursor = mDatabase.rawQuery("SELECT * from BookmarkFolderTab where uuid =?", new String[]{uuid});
            cursor = new BookmarkFolderCursorWrapper(tmpCursor);
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {//返回光标是否指向最后一行之后的位置。
                    nodes.add(cursor.getNodeInfo());
                    cursor.moveToNext();
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (tmpCursor != null) {
                tmpCursor.close();
            }
            //closeDB();
        }
        return nodes;
    }

    private void closeDB() {
        if (mDatabase != null) {
            mDatabase.close();
        }
    }

    /**
     * @param uuid    查询uuid，更新该uuid的bookmarkfolderinfo的显示名称
     * @param newName 新的显示名称
     */
    public void updateFolderName(String uuid, String newName) {
        mDatabase.execSQL("UPDATE BookmarkFolderTab SET folderName=? where uuid=?", new String[]{newName, uuid});
    }

    /**
     * @param uuid          文件夹自身的uuid
     * @param newParentuuid 该文件夹的新parentuuid
     *                      更改文件夹到另一个文件夹下，只需要更改文件夹的parentuuid就可以做到
     */
    public void updateFolderParentuuid(String uuid, String newParentuuid) {
        mDatabase.execSQL("UPDATE BookmarkFolderTab SET parentUuid=? where uuid=?", new String[]{newParentuuid, uuid});
    }

    public void deleteFolder(String uuid) {
        mDatabase.execSQL("DELETE  FROM BookmarkFolderTab WHERE uuid = ?", new String[]{uuid});
    }

    private ContentValues getContentValues(BookmarkFolderNode node) {
        ContentValues values = new ContentValues();
        values.put(BookmarkFolderDbSchema.FolderTable.childs.FOLDER, node.getFolderName());
        values.put(BookmarkFolderDbSchema.FolderTable.childs.UUID, node.getUUID());
        values.put(BookmarkFolderDbSchema.FolderTable.childs.PARENTUUID, node.getParentUUID());
        return values;
    }


}
