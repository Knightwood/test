package com.example.kiylx.ti.corebase;

public class WebPage_Info {
    //收集网页的信息，用于展示多窗口以及记录历史记录
    private String title;
    private String url;
    private int WEB_feature;
    private String date;
    private String bookmarkFolderName;
    /*WEB_feature：0，主页,不计入历史记录;，url指定为about:newTab
     *所以除了0以外的flags都是可以计入历史记录的
     *1，将载入网址，可以计入历史记录;
     *-1：网页被收藏;
     *-2表示这是一个给收藏网页分类的标签，这个网页标题是标签，网址是null*/
    //bookmarkFolderName,网页收藏时所用的标签


    /*public WebPage_Info clone(WebPage_Info info){
        WebPage_Info cloned=new WebPage_Info()
    }*/

    /**
     * @param title               标题
     * @param url                 网址
     * @param bookmarkFolderName 收藏网页时标识所属文件夹。
     * @param web_feature         标识是否被收藏了  -标识网页是属于什么类的，收藏，历史，为加载网址。以半废弃
     * @param date                网页加载时间
     */
    public WebPage_Info(String title, String url, String bookmarkFolderName, int web_feature, String date) {
        this.title = title;
        this.url = url;
        this.bookmarkFolderName = bookmarkFolderName;
        this.WEB_feature = web_feature;
        this.date = date;

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

    public int getWEB_feature() {
        return WEB_feature;
    }

    public void setWEB_feature(int WEB_feature) {
        this.WEB_feature = WEB_feature;
    }

    public void setDate(String s) {
        this.date = s;
    }

    public String getDate() {
        return this.date;
    }

    public String getBookmarkFolderName() {
        return this.bookmarkFolderName;
    }

    public void setBookmarkFolderName(String s) {
        this.bookmarkFolderName = s;
    }

}
