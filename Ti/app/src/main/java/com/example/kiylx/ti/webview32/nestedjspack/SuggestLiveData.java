package com.example.kiylx.ti.webview32.nestedjspack;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

/**
 * 创建者 kiylx
 * 创建时间 2020/7/13 18:29
 * 让js代码调用java代码时，把数据传出来，利用这个livedata，让某些类监听livedata更新界面
 */
public class SuggestLiveData extends LiveData<String[]> {
    private static SuggestLiveData liveData;
    private SuggestLiveData() {
    }
    public static SuggestLiveData getInstance(){
        if (liveData==null){
            synchronized(SuggestLiveData.class){
                if (liveData==null)
                    liveData=new SuggestLiveData();
            }
        }
        return liveData;
    }

    @Nullable
    @Override
    public String[] getValue() {
        return super.getValue();
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
    protected void postValue(String[] value) {
        super.postValue(value);
    }
}
