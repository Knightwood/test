package com.example.kiylx.ti.model;

import android.util.Log;

/**
 * 主界面的设置选项模型
 */
public class HomePageOptionsViewModel {
    private static final String TAGS="HomePageOptionViewModel";
    //选项名称
    private String optionsName;

    public HomePageOptionsViewModel() {

    }

    public String getOptionsName() {
        return optionsName;
    }

    public void setOptionsName(String optionsName) {
        this.optionsName = optionsName;
    }

}
