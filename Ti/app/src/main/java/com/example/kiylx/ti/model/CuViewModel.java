package com.example.kiylx.ti.model;

import android.app.Activity;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.view.View;
import android.webkit.WebView;

import java.util.ArrayList;
import java.util.List;

public class CuViewModel extends ViewModel {
    private MutableLiveData<ArrayList<View>> WebViewlist;
    private LiveData<Integer> WebViewCount = null;

    public CuViewModel(){

    }

    public MutableLiveData<ArrayList<View>> getWebViewlist(){
        if(WebViewlist==null){
             WebViewlist= new MutableLiveData<>();
        }
        return WebViewlist;
    }







}
