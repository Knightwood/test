package com.example.kiylx.ti.model;

import android.webkit.WebView;

import java.util.ArrayList;

public class Clist {
    //存着当前打开的所有webview对象
    private ArrayList<WebView> mArrayList;

    private volatile static Clist sClist;
    private Clist(){
        if(mArrayList ==null){
            mArrayList = new ArrayList<WebView>();
        }
    }
    public static Clist getInstance(){
        if(sClist==null){
            synchronized (Clist.class){
                if (sClist==null){
                    sClist=new Clist();
                }
            }
        }
        return sClist;
    }


    public void addToFirst(WebView v,int i){
        //添加到第一个位置，但是也可以指定i的值添加到其他位置
        insertToFirst(v,i);
    }
    private void insertToFirst(WebView v,int i){
        mArrayList.add(i,v);
    }

    public void delete(int i){
        remove1(i);
    }
    private void remove1(int i) {
        this.mArrayList.remove(i);
    }

    public int size(){
        return this.mArrayList.size();
    }
    public WebView getTop(int i){
        //返回第一个元素，但是也可以指定i的值获取其他位置的元素
        return mArrayList.get(i);
    }
    public void destroy(int pos){
        removeWeb(pos);

    }
    public boolean isempty(){
        return mArrayList.isEmpty();
    }
    private void removeWeb(int pos){
        WebView tmp=mArrayList.get(pos);
        if(tmp!=null){
            //先加载空内容
            tmp.setWebViewClient(null);
            tmp.setWebChromeClient(null);
            tmp.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            //清空历史
            tmp.clearHistory();
            //然后销毁
            tmp.destroy();
            //然后置为空
            mArrayList.remove(pos);
        }
    }
    public void stop(int i){mArrayList.get(i).onPause();}
    public void reStart(int i){mArrayList.get(i).onResume();}

}
