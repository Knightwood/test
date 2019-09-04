package com.example.kiylx.ti;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.kiylx.ti.favoritepageDataBase.FavoritepageBaseHelper;
import com.example.kiylx.ti.favoritepageDataBase.FavoritepageDbSchema;
import com.example.kiylx.ti.favoritepageDataBase.ItemCursorWrapper;

import java.util.ArrayList;
import java.util.List;

public class AboutStar {
    private static AboutStar sAboutStar;
    private static ArrayList<WebPage_Info> lists;
    private SQLiteDatabase mDatabase;
    private Context mContext;

    private AboutStar(Context context){
        mContext=context;
        lists=new ArrayList<>();
        mDatabase=new FavoritepageBaseHelper(mContext).getWritableDatabase();
    }
    public static AboutStar get(Context context){
        if(sAboutStar==null){
            sAboutStar=new  AboutStar(context);
        }
        return sAboutStar;
    }

    public void add(WebPage_Info info){
        ContentValues values = getContentValues(info);
        mDatabase.insert(FavoritepageDbSchema.FavoriteTable.NAME,null,values);

    }

    public WebPage_Info getWebPageinfo(String title){
ItemCursorWrapper cursor = queryFavority(FavoritepageDbSchema.FavoriteTable.childs.TITLE+"=?",new String[]{title});
try{
    if(cursor.getCount()==0){
        return null;
    }
    cursor.moveToFirst();
    return cursor.getFavoriterinfo();
}finally {
    cursor.close();
}
    }
    public List<WebPage_Info> getWebPageinfos(){
        ItemCursorWrapper cursor = queryFavority(null,null);
        ArrayList<WebPage_Info> mlists=new ArrayList<>();
        try{
            if(cursor.getCount()==0){
                return null;
            }
            cursor.moveToFirst();
            while(!cursor.isAfterLast()){
                mlists.add(cursor.getFavoriterinfo());
                cursor.moveToNext();
            }
        }finally {
            cursor.close();
        }
        return mlists;
    }
    private static ContentValues getContentValues(WebPage_Info info){
        ContentValues values = new ContentValues();
        values.put(FavoritepageDbSchema.FavoriteTable.childs.TITLE,info.getTitle());
        values.put(FavoritepageDbSchema.FavoriteTable.childs.url,info.getUrl());
        values.put(FavoritepageDbSchema.FavoriteTable.childs.FOLDERS,info.getFolders());

        return values;
    }
    private ItemCursorWrapper queryFavority(String whereClause, String[] whereArgs){
        Cursor cursor =mDatabase.query(
                FavoritepageDbSchema.FavoriteTable.NAME,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                null
        );
        return new ItemCursorWrapper(cursor);
    }
}
