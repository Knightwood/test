package com.example.kiylx.ti.mvp.contract;

import com.example.kiylx.ti.mvp.contract.base.BaseContract;

/**
 * 创建者 kiylx
 * 创建时间 2020/8/23 8:38
 * packageName：com.example.kiylx.ti.mvp.contract
 * 描述：
 */
public interface MainActivityContract extends BaseContract.View {
    /**
     * @param url 要分享的网址
     *            <p>
     *            分享当前的页面网址
     */
    void sharing(String url);

    /**
     * @param url 网址
     * 添加到书签
     * @param openEditView
     */
    void addtobookmark(String url, String title, boolean openEditView);

    /**
     * 重新载入当前网页
     */
    void reload();

    /**
     * 页内查找,提供查找当前webview视图内的文字
     */
    void openPageInsideSearchView();

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
