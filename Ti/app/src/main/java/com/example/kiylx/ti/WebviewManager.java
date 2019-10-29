package com.example.kiylx.ti;


import com.example.kiylx.ti.model.WebPage_Info;

import java.util.Observable;

public class WebviewManager extends Observable {
    private WebPage_Info info;

    public WebviewManager() {
    }

    public void setInfo(WebPage_Info info) {
        this.info = info;
        notifyChange(info);
    }

    public void notifyChange(WebPage_Info arg) {
        notifyObservers(arg);
    }

    public WebPage_Info getInfo() {
        return info;
    }
}
