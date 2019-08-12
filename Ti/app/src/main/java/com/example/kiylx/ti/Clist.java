package com.example.kiylx.ti;

import android.webkit.WebView;

import java.util.ArrayList;

public class Clist {
    private ArrayList<WebView> mArrayList;

    public Clist(){
        if(mArrayList ==null){
            mArrayList = new ArrayList<WebView>();
        }
    }


    public void addToFirst(WebView v,int i){
        //添加到第一个位置，但是也可以指定i的值添加到其他位置
        insertToFirst(v,i);
    }
    private void insertToFirst(WebView v,int i){
        mArrayList.add(i,v);
    }

    public void delete(int i){
        remove1(mArrayList,i);
    }
    private void remove1(ArrayList<WebView> r, int i) {
        this.mArrayList.remove(i);
    }

    public int size(){
        return this.mArrayList.size();
    }
    public WebView getTop(int i){
        //返回第一个元素，但是也可以指定i的值获取其他位置的元素
        return mArrayList.get(i);
    }

}
