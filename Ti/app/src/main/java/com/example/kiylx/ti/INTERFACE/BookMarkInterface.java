package com.example.kiylx.ti.INTERFACE;

import com.example.kiylx.ti.Corebase.WebPage_Info;

import java.util.ArrayList;

public interface BookMarkInterface {
    public ArrayList<WebPage_Info> getBookmark();
    public ArrayList<WebPage_Info> getBookmark(String tag);
    public void insert(WebPage_Info info);

}
