package com.example.kiylx.ti;

import java.util.ArrayList;

public class CurrentUse_WebPage_Lists {
    //用来展示多窗口的网页数量的列表
    private static CurrentUse_WebPage_Lists sCurrentUse_webPage_lists;
    private ArrayList<WebPage_Info> mCurrectList;

    private CurrentUse_WebPage_Lists() {
        mCurrectList = new ArrayList<>();
    }
    public static CurrentUse_WebPage_Lists get(){
        if (sCurrentUse_webPage_lists ==null){
            sCurrentUse_webPage_lists=new CurrentUse_WebPage_Lists();
        }
        return sCurrentUse_webPage_lists;
    }

    public void add(String title, String url,int flags){
        WebPage_Info info = new WebPage_Info(title, url,flags);
        mCurrectList.add(0,info);
        //在0处添加元素，与webview添加进clist的顺序保持一致
    }
    public void delete(int index){
        mCurrectList.remove(index);
    }
    public ArrayList<WebPage_Info> getPageList(){
        return mCurrectList;
    }
}
