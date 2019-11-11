package com.example.kiylx.ti.Core1;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.kiylx.ti.FavoritePageDataBase.TagDbSchema;
import com.example.kiylx.ti.FavoritePageDataBase.TagItemCursorWrapper;
import com.example.kiylx.ti.FavoritePageDataBase.TagOpenHelper;

import java.util.ArrayList;

public class AboutTag {
    /*tag，如果在添加标签的时候选择的是未分类，那么这一条*/
    private static AboutTag sAboutTag;
    private SQLiteDatabase mDatabase;
    private ArrayList<String> taglists;
    /*taglist是一个全局的列表，
    会从数据库里拿数据后添加进taglist返回给需要的对象，
    所以，必要时需要判断taglist是不是空的再决定是否从数据库里拿数据放进去*/
    /*添加或是删除时，会让数据库和taglists保持一致，因为其他类的lists也是taglist的引用，所以不需要做额外工作*/

    private AboutTag(Context context) {

        mDatabase = new TagOpenHelper(context, TagDbSchema.TagTable.NAME, null, 1).getWritableDatabase();
        taglists = new ArrayList<>();
        taglists.add(0, "未分类");
    }

    public static AboutTag get(Context context) {
        if (null == sAboutTag) {
            sAboutTag = new AboutTag(context);
        }
        return sAboutTag;
    }

    private void addTagintoLists(String str) {
        if (!str.equals("未分类"))
            taglists.add(str);
    }

    private void deleteTagfromLists(String str) {
        if (!str.equals("未分类"))
            taglists.remove(str);
    }

    /**
     * @param str “标签”名称
     * @return 返回“标签”在taglist中的位置
     * 以此来设置spinner显示特定项
     * 如果网页信息中tag是空的，那就返回0，显示成：“未分类”。
     */
    public int getPosfromLists(String str) {
            if (str == null) {
            return 0;
        }
        return taglists.indexOf(str);
    }

    /**
     * @param pos 位置
     * @return 返回存储“标签”的list中pos这个位置存储的“标签”
     */
    public String getTagfromList(int pos) {
        return taglists.get(pos);
    }

    /**
     * @return 获取的taglist的大小
     */
    public int getSize() {
        return taglists.size();
    }

    /**
     * @param tag 要查询的“标签”名称
     * @return 如果要查询的“标签”存在，返回真
     * 查询是否有与形参“tag”重复的元素
     */
    private boolean isExist(String tag) {

        return taglists.contains(tag);
    }

    /**
     * @param oldTag 原“标签”
     * @param newTag 要修改为的新“标签”
     */
    private void updateTaginList(String oldTag, String newTag) {
        taglists.set(taglists.indexOf(oldTag), newTag);
    }

    public ArrayList<String> getTaglists() {
        return taglists;
    }

    //=============================以下数据库操作===================//
    public void add(String tag) {
        if (isExist(tag)) {
            return;
        }
        Log.d("tag添加", tag);
        ContentValues values = getContentValues(tag);
        mDatabase.insert(TagDbSchema.TagTable.NAME, null, values);
        addTagintoLists(tag);

    }

    public void delete(String tag) {
        mDatabase.delete(TagDbSchema.TagTable.NAME, TagDbSchema.TagTable.childs.TAG + " =? ", new String[]{tag});
        deleteTagfromLists(tag);

    }

    public void updateTag(String oldTag, String newTag) {
        //更新tag为新的名称
        //oldTag:原tag;newTag:要改成的名称
        mDatabase.update(TagDbSchema.TagTable.NAME, getContentValues(newTag), TagDbSchema.TagTable.childs.TAG + " =?", new String[]{oldTag});
        // 表名
        // 修改后的数据
        // 修改条件
        // 满足修改的值
        updateTaginList(oldTag, newTag);//实时刷新taglist中的值
    }

    private static ContentValues getContentValues(String tag) {
        //存tag
        ContentValues values = new ContentValues();
        values.put(TagDbSchema.TagTable.childs.TAG, tag);

        return values;
    }

    public ArrayList<String> getTagListfromDB() {
        TagItemCursorWrapper cursor = queryTag(null, null);
        //如果taglist仅有一个“未分类，那有两种情况，一种是数据库里没有其他标签，一种是taglist仅被初始化还没有从数据库里拿数据
        if (taglists.size() == 1)
            try {
                Log.d("TagDB数量", String.valueOf(cursor.getCount()));
                if (cursor.getCount() == 0) {
                    return taglists;
                }
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    taglists.add(cursor.getTaginfo());
                    cursor.moveToNext();
                }
            } finally {
                cursor.close();
            }
        return taglists;

    }

    private TagItemCursorWrapper queryTag(String where, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                TagDbSchema.TagTable.NAME,
                null,
                where,
                whereArgs,
                null,
                null,
                null);
        return new TagItemCursorWrapper(cursor);
    }
}
