package com.example.kiylx.ti.model;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class Converted_WebPage_Lists implements Observer {
    /*此类用来存储浏览网页产生的网页信息，是实时从webview中抽取信息，存储。*/
    //用来展示多窗口的网页数量的列表，当前打开的所有网页信息
    private ArrayList<WebPage_Info> mCurrectList;
    private Observable mObservable;

    private static Converted_WebPage_Lists sConverted_webPage_lists;

    private Converted_WebPage_Lists() {
        mCurrectList = new ArrayList<>();
    }

    private Converted_WebPage_Lists(Observable o) {
        mCurrectList = new ArrayList<>();
        this.mObservable = o;
        mObservable.addObserver(this);
    }

    public static Converted_WebPage_Lists get() {
        if (sConverted_webPage_lists == null) {
            sConverted_webPage_lists = new Converted_WebPage_Lists();
        }
        return sConverted_webPage_lists;
    }

    public static Converted_WebPage_Lists get(Observable o) {
        if (sConverted_webPage_lists == null) {
            sConverted_webPage_lists = new Converted_WebPage_Lists(o);
        }
        return sConverted_webPage_lists;
    }

    public void add(String title, String url, int flags) {
        WebPage_Info info = new WebPage_Info(title, url, flags);
        mCurrectList.add(0, info);
        //在0处添加元素，与webview添加进clist的顺序保持一致
    }

    public void add(String title, String url, String tags, int flags) {
        WebPage_Info info = new WebPage_Info(title, url, tags, flags);
        mCurrectList.add(0, info);
        //在0处添加元素，与webview添加进clist的顺序保持一致
    }

    public void delete(int index) {
        mCurrectList.remove(index);
    }

    public ArrayList<WebPage_Info> getPageList() {
        return mCurrectList;
    }

    public int getPosition(WebPage_Info item) {
        return mCurrectList.indexOf(item);
    }

    public String getTitle(int pos) {
        return mCurrectList.get(pos).getTitle();
    }

    public void setTitle(int pos, String s) {
        mCurrectList.get(pos).setTitle(s);
    }

    public String getUrl(int pos) {
        return mCurrectList.get(pos).getUrl();
    }

    public void setUrl(int pos, String url) {
        mCurrectList.get(pos).setUrl(url);
    }

    public void setFlags(int pos, int i) {
        mCurrectList.get(pos).setFlags(i);
    }

    public WebPage_Info getInfo(int i) {
        return mCurrectList.get(i);
    }

    public String getdate(int i) {
        return mCurrectList.get(i).getDate();
    }

    public void setdate(int i) {
        mCurrectList.get(i).setDate(TimeProcess.getTime());
    }

    @Override
    public void update(Observable o, Object arg) {

    }
}
