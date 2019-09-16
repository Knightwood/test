package com.example.kiylx.ti;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.kiylx.ti.favoritepageDataBase.FavoritepageDbSchema;
import com.example.kiylx.ti.favoritepageDataBase.ItemCursorWrapper;
import com.example.kiylx.ti.favoritepageDataBase.TagDbSchema;
import com.example.kiylx.ti.favoritepageDataBase.TagOpenHelper;

import java.util.ArrayList;

public class AboutTag {
    private static AboutTag sAboutTag;
    private SQLiteDatabase mDatabase;

    public AboutTag(Context context) {
        mDatabase = new TagOpenHelper(context, TagDbSchema.TagTable.NAME,null,1).getWritableDatabase();
    }
    public static AboutTag get(Context context){
        if(null==sAboutTag){
            sAboutTag=new AboutTag(context);
        }
        return sAboutTag;
    }

    public void add(String tag){
        ContentValues values = getContentValues(tag);
        mDatabase.insert(FavoritepageDbSchema.FavoriteTable.NAME,null,values);

    }
    private static ContentValues getContentValues(String tag){
        //存tag
        ContentValues values = new ContentValues();
        values.put(TagDbSchema.TagTable.childs.TAG,tag);

        return values;
    }
    public void delete(String tag){
        mDatabase.delete(TagDbSchema.TagTable.NAME, TagDbSchema.TagTable.childs.TAG,new String[]{tag});

    }
    public void updateTag(String tag, String tag2){
        //tag:原tag;tag2:要改成的目标tag
        mDatabase.update(TagDbSchema.TagTable.NAME,getContentValues(tag2),TagDbSchema.TagTable.childs.TAG,new String[]{tag});
        // 表名
        // 修改后的数据
        // 修改条件
        // 满足修改的值
    }
    public String getItem(String tag){
        String result;
        ItemCursorWrapper cursor = queryTag(TagDbSchema.TagTable.childs.TAG,new String[]{tag});
        try{
            if(cursor.getCount()==0){
                return null;
            }
            cursor.moveToFirst();
            result=cursor.getTaginfo();
        }finally {
            cursor.close();
        }
        return result;
    }

    public ArrayList<String> getItems(){
        ItemCursorWrapper cursor = queryTag(null,null);
        ArrayList<String> lists = new ArrayList<>();
        try{
            if(cursor.getCount()==0){
                return null;
            }
            cursor.moveToFirst();
            while (!cursor.isAfterLast()){
                lists.add(cursor.getTaginfo());
            }
        }finally {
            cursor.close();
        }
        return lists;

    }
    private ItemCursorWrapper queryTag(String where,String[] whereArgs){
        Cursor cursor = mDatabase.query(TagDbSchema.TagTable.NAME,
                null,
                where,
                whereArgs,
                null,
                null,
                null);
        return new ItemCursorWrapper(cursor);
    }
}
