package com.example.kiylx.ti;

import android.database.sqlite.SQLiteDatabase;

public class WebPage_Info {
    //收集网页的信息，用于展示多窗口以及记录历史记录
    private String title;
    private String url;
    private int flags=0;
    private String date;
    //flags：0，主页,不计入历史记录。1，载入了网址，计入历史记录

    WebPage_Info(String title, String url, int flags) {
        this.title = title;
        this.url = url;
        this.flags=flags;
        this.date=null;
    }
     public WebPage_Info(String title, String url,String date) {
        this.title = title;
        this.url = url;
        this.date=date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getFlags() {
        return flags;
    }

    public void setFlags(int flags) {
        this.flags = flags;
    }

    public void setDate(String s){
        this.date=s;
    }
    public String getDate(){
        return this.date;
    }
}
