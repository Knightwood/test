package com.example.kiylx.ti;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.BaseAdapter;

import com.example.kiylx.ti.favoritepageDataBase.FavoritePageBaseHelper;
import com.example.kiylx.ti.favoritepageDataBase.FavoritepageDbSchema;
import com.example.kiylx.ti.favoritepageDataBase.ItemCursorWrapper;
import com.example.kiylx.ti.model.WebPage_Info;

import java.util.ArrayList;

public class AboutBookmark {
    private static AboutBookmark sAboutBookmark;
    private static ArrayList<WebPage_Info> bookMarklists;
    private SQLiteDatabase mDatabase;
    private Context mContext;

    private AboutBookmark(Context context){
        mContext=context;
        bookMarklists =new ArrayList<>();//暂时没有用
        mDatabase=new FavoritePageBaseHelper(mContext,FavoritepageDbSchema.FavoriteTable.NAME,null,1).getWritableDatabase();
    }
    public static AboutBookmark get(Context context){
        if(sAboutBookmark==null){
            sAboutBookmark=new  AboutBookmark(context);
        }
        return sAboutBookmark;
    }

//==========================以下数据库操作=========================//
    public void add(WebPage_Info info){
        if(info==null||info.getUrl()==null){
            return;
        }
        ContentValues values = getContentValues(info);
        mDatabase.insert(FavoritepageDbSchema.FavoriteTable.NAME,null,values);

    }
    public void updateItem(WebPage_Info info){
        //更新条目
        //如果url未被改变，只需要更新数据，否则算作是新的收藏而被加入到数据库
        String url = info.getUrl();
        ContentValues values = getContentValues(info);
        mDatabase.update(FavoritepageDbSchema.FavoriteTable.NAME,values, FavoritepageDbSchema.FavoriteTable.childs.url+"=?",new String[]{url});

    }
    public ArrayList<WebPage_Info> getWebPageinfos(){
        //第一个参数来指示查询哪一列

        ArrayList<WebPage_Info> mlists=new ArrayList<>();//用来放查找结果
        ItemCursorWrapper cursor= queryFavority(null,null);
        try{
            if(cursor.getCount()==0){
                return mlists;
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
    public ArrayList<WebPage_Info> getBookmarkitems(String str){
        ArrayList<WebPage_Info> mlists=new ArrayList<>();//用来放查找结果
        ItemCursorWrapper cursor= queryFavority(FavoritepageDbSchema.FavoriteTable.childs.TAG + " =?",new String[]{str});
        try{
            if(cursor.getCount()==0){
                return mlists;
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
    public void delete(String url){
        mDatabase.delete(FavoritepageDbSchema.FavoriteTable.NAME, FavoritepageDbSchema.FavoriteTable.childs.url + " =?",new String[]{url});
    }
    private static ContentValues getContentValues(WebPage_Info info){
        //存网页信息
        ContentValues values = new ContentValues();
        values.put(FavoritepageDbSchema.FavoriteTable.childs.TITLE,info.getTitle());
        values.put(FavoritepageDbSchema.FavoriteTable.childs.url,info.getUrl());
        values.put(FavoritepageDbSchema.FavoriteTable.childs.TAG,info.getWebTags());

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
    public boolean isMarked(WebPage_Info info){
        //判断标准是网址，与数据库里网址一致即为收藏了
        String url=info.getUrl();
        ItemCursorWrapper cursor=queryFavority(FavoritepageDbSchema.FavoriteTable.childs.url + " =?",new String[]{url});
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

    public ArrayList<WebPage_Info> getChangeLists(String str) {
        //返回tag的书签list
        if(str.equals("所有书签")){
            return getWebPageinfos();
        }
        return getBookmarkitems(str);
    }
    public void updateTagsforItems(String tag, String newTagname){
        //根据tag批量更改条目的信息,更新书签记录的tag名称

    }
    public void deleteWithtag(String tag) {
        //根据tag这个标签删除相关的条目
    }
}
/*
    public void WriteContent(Context context, String content, String filename){

        //String filename = "TAG_0";
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

    private String[] execString(StringBuilder sb) {
        //分割从文件中读取到的字符串
        String tmp= sb.toString();
        String[] result = new String[0];
        if(tmp.contains("/")){
            result=tmp.split("/");
        }
        return result;
    }

    public boolean fileExist() {
        return false;
    }

    public String[] getDataFromFile(Context context, String filename) {

        try {
            //用StringBuilder来接收数据，而不是用String+=的方法。
            StringBuilder sb = new StringBuilder();
            //每次读取1024个byte的数据
            byte[] bytes = new byte[1024];
            FileInputStream inputStream = context.openFileInput(filename);
            int len;
            while ((len = inputStream.read(bytes)) != -1) {
                sb.append(new String(bytes, 0, len));
            }
            Log.e("读取到的数据", sb.toString());
            //返回处理后的字符数组
            return execString(sb);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    public boolean TagIsExist(Context context,String tag,String filename){
        OutputStream outputStream;
        try{
            outputStream=context.openFileOutput(filename,Context.MODE_PRIVATE);

        }
    }*/