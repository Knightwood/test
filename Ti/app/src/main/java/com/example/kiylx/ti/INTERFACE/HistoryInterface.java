package com.example.kiylx.ti.INTERFACE;

import com.example.kiylx.ti.Corebase.WebPage_Info;

import java.util.ArrayList;

/**
 * 历史记录和收藏操作的接口
 */
public interface HistoryInterface {
    public WebPage_Info getData();
    public ArrayList<WebPage_Info> getDataLists();
    public ArrayList<WebPage_Info> getDataLists(String startdate,String endDate);
    public void addData(WebPage_Info info);
    public void updateData(WebPage_Info info);
}
