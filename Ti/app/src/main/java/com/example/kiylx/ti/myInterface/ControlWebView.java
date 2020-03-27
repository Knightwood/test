package com.example.kiylx.ti.myInterface;

/**
 * 创建者 kiylx
 * 创建时间 2020/3/27 13:04
 * MainActivity实现此接口，把接口交给minsetDialog，使其调用。
 * 以及可以把实现的方法交给HanderClickedLinked接口使用
 */
public interface ControlWebView {
    void sharing(String url);//分享网址
    void addtobookmark(String url);//添加到书签
    void reload();//重新载入网页
    void searchText();//页内查找
    void usePcMode();//使用电脑模式
    void newPage(String url);//新窗口打开网页
}
