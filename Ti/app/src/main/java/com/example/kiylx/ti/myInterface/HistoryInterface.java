package com.example.kiylx.ti.myInterface;

import com.example.kiylx.ti.corebase.WebPage_Info;

import java.util.ArrayList;
import java.util.List;

/**
 * 历史记录和收藏操作的接口
 */
public interface HistoryInterface {

    List<WebPage_Info> getDataLists();

    List<WebPage_Info> getDataLists(String startdate, String endDate);

    void addData(WebPage_Info info);

    void delete(WebPage_Info info);

    void deleteAll();
}
