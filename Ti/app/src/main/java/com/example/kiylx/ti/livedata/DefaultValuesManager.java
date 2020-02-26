package com.example.kiylx.ti.livedata;

import androidx.lifecycle.MutableLiveData;

/**
 * @创建者 kiylx
 * @创建时间 2020/2/26 21:19
 */
public class DefaultValuesManager {
    private static DefaultValuesManager manager;
    private MutableLiveData<String> userAgent;

    public static DefaultValuesManager getInstance(){
        if (manager==null){
            synchronized (DefaultValuesManager.class){
                if (manager==null){
                    manager=new DefaultValuesManager();
                }
            }
        }
        return manager;
    }
    private DefaultValuesManager(){

    }

    public MutableLiveData<String> getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent.setValue(userAgent);
        //this.userAgent = userAgent;
    }
}
