package com.example.kiylx.ti;

import android.webkit.WebView;

import java.util.ArrayList;

public class Clist {
    private ArrayList<WebView> r;

    public Clist(){
        if(r==null){
            r= new ArrayList<WebView>();
        }
    }
    public void add1(WebView v){
        insert(v);
    }
    private void insert(WebView vi){
        r.add(vi);
    }
    public void delete1(int i){
        remove1(r,i);
    }

    private void remove1(ArrayList<WebView> r, int i) {
        this.r.remove(i);
    }
    public int size(){
        return this.r.size();
    }

    public WebView getTop(){
        return r.get(r.size()-1);
    }


}
