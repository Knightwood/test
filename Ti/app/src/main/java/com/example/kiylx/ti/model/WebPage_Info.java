package com.example.kiylx.ti.model;

public class WebPage_Info {
    //收集网页的信息，用于展示多窗口以及记录历史记录
    private String title;
    private String url;
    private int flags=0;
    private String date;
    private String webTag;
    //flags：0，主页,不计入历史记录;
    //所以除了0以外的flags都是可以计入历史记录的
    // 1，将载入网址，可以计入历史记录;
    // -1：网页被收藏;
    // -2表示这是一个给收藏网页分类的标签，这个网页标题是标签，网址是null

    public WebPage_Info(String title, String url, int flags) {
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
    public WebPage_Info(String title, String url, String webTag, int flags) {
        this.title = title;
        this.url = url;
        this.date=null;
        this.webTag = webTag;
        this.flags = flags;

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

    public String getWebTags(){
        return this.webTag;
    }
    public void setWebTag(String s){
        this.webTag =s;
    }

}
