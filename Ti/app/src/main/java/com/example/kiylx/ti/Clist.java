package com.example.kiylx.ti;

import android.view.View;
import android.webkit.WebView;

import java.util.ArrayList;

public class Clist {
    private ArrayList<View> r;

    public Clist(){
        if(r==null){
            r= new ArrayList<View>();
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

    private void remove1(ArrayList<View> r, int i) {
        this.r.remove(i);
    }
    public int size(){
        return this.r.size();
    }
    public WebView getTop(){
        return ;
    }


}
