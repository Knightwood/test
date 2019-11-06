package com.example.kiylx.ti.INTERFACE;

import com.example.kiylx.ti.Corebase.WebPage_Info;

import java.util.ArrayList;

public interface BookMarkInterface {
    ArrayList<WebPage_Info> getBookmark();

    ArrayList<WebPage_Info> getBookmark(String tag);

    void insert(WebPage_Info info);

}
