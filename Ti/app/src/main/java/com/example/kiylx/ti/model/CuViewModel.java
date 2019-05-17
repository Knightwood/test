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
    private MutableLiveData<ArrayList<View>> webViewlist;
    private LiveData<Integer> webViewCount = null;
    private MutableLiveData<ArrayList<View>> webpage;



    public MutableLiveData<ArrayList<View>> getWebViewlist(){
        if(webViewlist==null){
             webViewlist= new MutableLiveData<>();
        }
        return webViewlist;
    }
    public MutableLiveData<ArrayList<View>> getWebpage(){
        if(webpage==null){
            webpage=new MutableLiveData<>();
        }
        return webpage;
    }


}
