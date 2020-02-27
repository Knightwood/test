package com.example.kiylx.ti.livedata;

import androidx.lifecycle.LiveData;

/**
 * @创建者 kiylx
 * @创建时间 2020/2/27 15:45
 */
public class DefaultValueLiveData extends LiveData<DefaultValue> {
    private static DefaultValueLiveData mLiveData;

    private DefaultValueLiveData(){

    }

    public static DefaultValueLiveData getInstance(){
        if (mLiveData==null){
            synchronized (DefaultValueLiveData.class){
                if (mLiveData==null){
                  mLiveData=new DefaultValueLiveData();
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
    protected void setValue(DefaultValue value) {
        super.setValue(value);
    }

    public void setmLiveData(DefaultValue value){
        mLiveData.setValue(value);
    }
}
