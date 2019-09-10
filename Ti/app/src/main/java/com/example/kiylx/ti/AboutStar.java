package com.example.kiylx.ti;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.content.Context;

import com.example.kiylx.ti.favoritepageDataBase.FavoritepageBaseHelper;
import com.example.kiylx.ti.favoritepageDataBase.FavoritepageDbSchema;
import com.example.kiylx.ti.favoritepageDataBase.ItemCursorWrapper;
import com.example.kiylx.ti.model.CustomWebviewClient;
import com.example.kiylx.ti.model.WebPage_Info;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AboutStar implements CustomWebviewClient.ISSTAR {
    private static AboutStar sAboutStar;
    private static ArrayList<WebPage_Info> lists;
    private SQLiteDatabase mDatabase;
    private Context mContext;

    private AboutStar(Context context){
        mContext=context;
        lists=new ArrayList<>();//暂时没有用
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
    public void updateItem(WebPage_Info info){
        //更新条目
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
        ArrayList<WebPage_Info> mlists=new ArrayList<>();//用来放查找结果
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

    public void createTagFile(Context context,String content){
        String filename = "TAG_0";
        //content要保存的tag

        FileOutputStream outputStream;

        try {
            //openFileOutput需要context才能调用，所以这里没有写在activity中就需要自己放一个context去调用openFileOutput
            outputStream = context.openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(content.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getDataFromInternalPath(Context context) {
        try {
            //用StringBuilder来接收数据，而不是用String+=的方法。
            StringBuilder sb = new StringBuilder();
            //每次读取1024个byte的数据
            byte[] bytes = new byte[1024];
            FileInputStream inputStream = context.openFileInput("TAG_0");
            int len = 0;
            while ((len = inputStream.read(bytes)) != -1) {
                sb.append(new String(bytes, 0, len));
            }
            execString(sb);
            Log.e("读取到的数据", sb.toString());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String[] execString(StringBuilder sb) {
        String tmp= sb.toString();
        String[] result = new String[0];
        if(tmp.contains("/")){
            result=tmp.split("/");
        }
        return result;
    }
    @Override
    public boolean isStar(WebPage_Info info){
        String url=info.getUrl();
        ItemCursorWrapper cursor=queryFavority(FavoritepageDbSchema.FavoriteTable.childs.url+"=?",new String[]{url});
        try{
            if(cursor.getCount()==0){
                //如果查询得到的结果是0个，那就返回flase，表示这个网页还没有被收藏
                return false;
            }

            return true;
        }finally {
            cursor.close();
        }
    }
}
