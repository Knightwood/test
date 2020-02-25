package com.example.kiylx.ti.core1;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/**
 * @创建者 kiylx
 * @创建时间 2020/2/25 21:55
 */
public class DefaultValuesViewModel extends ViewModel {

    private MutableLiveData<String> userAgent;

    public MutableLiveData<String> getUserAgent(){
        if (userAgent==null){
            userAgent=new MutableLiveData<>();
        }
        return userAgent;
    }

}
