package com.example.kiylx.ti.model;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.v4.app.Fragment;
import android.view.View;

import java.util.ArrayList;

public class CuViewModel extends ViewModel {
    private MutableLiveData<ArrayList<View>> webViewlist;
    private LiveData<Integer> webViewCount = null;
    private MutableLiveData<ArrayList<Fragment>> fm_page;




    public MutableLiveData<ArrayList<View>> getWebViewlist(){
        if(webViewlist==null){
             webViewlist= new MutableLiveData<>();
        }
        return webViewlist;
    }
    public MutableLiveData<ArrayList<Fragment>> getFm_page(){
        if(fm_page ==null){
            fm_page =new MutableLiveData<>();
        }
        return fm_page;
    }


}
