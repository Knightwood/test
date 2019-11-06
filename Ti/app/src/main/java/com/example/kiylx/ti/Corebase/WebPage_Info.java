package com.example.kiylx.ti.Corebase;

public class WebPage_Info {
    //收集网页的信息，用于展示多窗口以及记录历史记录
    private String title;
    private String url;
    private int WEB_feature = 0;
    private String date;
    private String webview_marked_name;
    /*WEB_feature：0，主页,不计入历史记录;
     *所以除了0以外的flags都是可以计入历史记录的
     *1，将载入网址，可以计入历史记录;
     *-1：网页被收藏;
     *-2表示这是一个给收藏网页分类的标签，这个网页标题是标签，网址是null*/
    //webview_marked_name,网页收藏时所用的标签


    /*public WebPage_Info clone(WebPage_Info info){
        WebPage_Info cloned=new WebPage_Info()
    }*/

    /**
     * @param title               标题
     * @param url                 网址
     * @param webview_marked_name 网页的标签(tag),用于收藏网页时使用。
     * @param web_feature         标识网页是属于什么类的，收藏，历史，为加载网址。以半废弃
     * @param date                网页加载时间
     */
    public WebPage_Info(String title, String url, String webview_marked_name, int web_feature, String date) {
        this.title = title;
        this.url = url;
        this.webview_marked_name = webview_marked_name;
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

    public String getWebTags() {
        return this.webview_marked_name;
    }

    public void setWebview_marked_name(String s) {
        this.webview_marked_name = s;
    }

}
