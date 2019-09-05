package com.example.kiylx.ti.model;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.fragment.app.Fragment;
import android.view.View;

import com.example.kiylx.ti.Clist;

import java.util.ArrayList;

public class CuViewModel extends ViewModel {
    private MutableLiveData<ArrayList<View>> webViewlist;
    private LiveData<Integer> webViewCount = null;
    private MutableLiveData<ArrayList<Fragment>> fm_page;
    private MutableLiveData<Clist> list_r;




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
