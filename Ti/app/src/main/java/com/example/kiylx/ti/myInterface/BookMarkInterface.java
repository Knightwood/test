package com.example.kiylx.ti.myInterface;

import com.example.kiylx.ti.corebase.WebPage_Info;

import java.util.ArrayList;

public interface BookMarkInterface {
    ArrayList<WebPage_Info> getBookmark();

    ArrayList<WebPage_Info> getBookmarks(String tag);

    /**
     * @param info 要添加进数据库的WebPage_Info
     */
    void insert(WebPage_Info info);

    void update(String url);

    void updateItem(WebPage_Info info);

    void delete(String url);

    /**
     * @param info WebPage_Info
     * @return 是否被收藏
     */
    boolean isMarked(WebPage_Info info);

    /**
     * @param tag tag名称，根据它来删除它下面的书签
     */
    void deleteBookMarkfromTag(String tag);

    /**
     * @param tag        旧的标签名称
     * @param newTagname 新的标签名称
     *                   会把旧的标签名称改为新的标签名称，并更新这个标签下的书签的记录
     */
    void updateItemWithTag(String tag, String newTagname);

}
