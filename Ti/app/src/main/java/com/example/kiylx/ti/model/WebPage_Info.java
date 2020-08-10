package com.example.kiylx.ti.model;

import java.util.UUID;

public class WebPage_Info {
    //收集网页的信息，用于展示多窗口以及记录历史记录
    private String uuid;//书签本身的uuid，标识其唯一性
    private String title;
    private String url;
    private int WEB_feature;
    private String date;
    private String bookmarkFolderUUID;//这是书签所属的文件夹的名称，它不是显示的name，是文件夹节点的uuid
    private int progress;//网页加载进度
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
     * @param title              标题
     * @param url                网址
     * @param bookmarkFolderUUID 收藏网页时标识所属文件夹。
     * @param web_feature        标识是否被收藏了  -标识网页是属于什么类的，收藏，历史，为加载网址。以半废弃
     *                           使用的地方：1，在webviewManager中会写为0，传入webpageinfo到webViewInfo_Manager类
     *                           2，在收藏里没有使用，所以默认给一个0就可以
     *                           3.Fragment_DoSearch中有使用
     * @param date               网页加载时间
     */
    public WebPage_Info(String title, String url, String bookmarkFolderUUID, int web_feature, String date) {
        this.title = title;
        this.url = url;
        this.bookmarkFolderUUID = bookmarkFolderUUID;
        this.WEB_feature = web_feature;
        this.date = date;

    }

    public WebPage_Info(String URL) {
        this("", URL, null, 0, null);
    }

    public WebPage_Info(String id, String title, String url, String folder) {
        this.uuid = id;
        this.title = title;
        this.url = url;
        this.bookmarkFolderUUID = folder;
    }

    /**
     * @param URL     网址
     * @param feature 标志，0或者-1都可以，0在不计入收藏时，-1是计入收藏时写入的，但是在收藏记录那，压根就没用过。所以，以后要删除这个标志
     */
    public WebPage_Info(String URL, int feature) {
        this("", URL, null, feature, null);
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

    public String getBookmarkFolderUUID() {
        return this.bookmarkFolderUUID;
    }

    public void setBookmarkFolderUUID(String s) {
        this.bookmarkFolderUUID = s;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid.toString();
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }
}
