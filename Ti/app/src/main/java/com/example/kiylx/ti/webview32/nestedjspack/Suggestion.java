package com.example.kiylx.ti.webview32.nestedjspack;

import android.webkit.JavascriptInterface;

/**
 * 提供js映射，让提供搜索建议的js调用此类中的方法，把数据传出来
 */
public class Suggestion {
    SuggestLiveData liveData;

    public Suggestion() {
        liveData = SuggestLiveData.getInstance();
    }

    @JavascriptInterface
    public void giveSuggest(String[] result) {
        liveData.postValue(result);
    }
}
