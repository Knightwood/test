package com.example.kiylx.ti.interfaces;

import androidx.fragment.app.DialogFragment;

/**
 * 创建者 kiylx
 * 创建时间 2020/3/27 13:04
 * MainActivity实现此接口，把接口交给minsetDialog，使其调用。
 * 以及可以把实现的方法交给HanderClickedLinked接口使用
 */
public interface ControlWebView {
    /**
     * @param url 要分享的网址
     *            <p>
     *            分享当前的页面网址
     */
    void sharing(String url);

    /**
     * @param url 网址
     * 添加到书签
     */
    void addtobookmark(String url);

    /**
     * 重新载入当前网页
     */
    void reload();

    /**
     * 页内查找
     */
    void searchText();

    /**
     * 电脑模式
     */
    void usePcMode();

    /**
     * @param url 网址
     * 新窗口打开网页
     */
    void newPage(String url);

    /**
     * 把网页存为mht文件
     */
    void saveWeb();

    /**
     * 把网页打印成pdf
     */
    void printPdf();

}
