package com.example.kiylx;

import android.graphics.drawable.Drawable;

public class AppInfo {
    String appName;
    Drawable drawable;
    public AppInfo(){}
    public AppInfo(String appName){
        this.appName = appName;
    }
    public AppInfo(String appName, Drawable drawable){
        this.appName = appName;
        this.drawable =drawable;
    }

    public String getAppName() {
        return appName;
    }

    public Drawable getDrawable() {
        return drawable;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public void setDrawable(Drawable drawable) {
        this.drawable = drawable;
    }
}
