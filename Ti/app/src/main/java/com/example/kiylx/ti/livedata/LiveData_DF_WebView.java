package com.example.kiylx.ti.livedata;

import androidx.lifecycle.LiveData;

/**
 * @创建者 kiylx
 * @创建时间 2020/2/27 15:45
 */
public class LiveData_DF_WebView extends LiveData<DefaultValue_WebView> {
    private static LiveData_DF_WebView mLiveData;

    private LiveData_DF_WebView(){

    }

    public static LiveData_DF_WebView getInstance(){
        if (mLiveData==null){
            synchronized (LiveData_DF_WebView.class){
                if (mLiveData==null){
                  mLiveData=new LiveData_DF_WebView();
                }
            }
        }
        return mLiveData;
    }

    @Override
    protected void onActive() {
        super.onActive();
    }

    @Override
    protected void onInactive() {
        super.onInactive();
    }

    @Override
    protected void setValue(DefaultValue_WebView value) {
        super.setValue(value);
    }

    public void setmLiveData(DefaultValue_WebView value){
        mLiveData.setValue(value);
    }

}
