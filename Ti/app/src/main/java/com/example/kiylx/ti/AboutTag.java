package com.example.kiylx.ti;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.example.kiylx.ti.favoritepageDataBase.FavoritePageBaseHelper;
import com.example.kiylx.ti.favoritepageDataBase.FavoritepageDbSchema;
import com.example.kiylx.ti.favoritepageDataBase.TagDbSchema;

public class AboutTag {
    AboutTag sAboutTag;
    private SQLiteDatabase mDatabase;

    public AboutTag(Context context) {
        mDatabase = new FavoritePageBaseHelper(context, TagDbSchema.TagTable.NAME,null,1).getWritableDatabase();
    }
    public void get(Context context){
        if(null==sAboutTag){
            sAboutTag=new AboutTag(context);
        }
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
}
