package com.example.kiylx.ti.INTERFACE;

import com.example.kiylx.ti.Corebase.WebPage_Info;

import java.util.ArrayList;

/**
 * 历史记录和收藏操作的接口
 */
public interface HistoryInterface {

    ArrayList<WebPage_Info> getDataLists();

    ArrayList<WebPage_Info> getDataLists(String startdate, String endDate);

    void addData(WebPage_Info info);

    void delete(WebPage_Info info);

    void deleteAll();
}
