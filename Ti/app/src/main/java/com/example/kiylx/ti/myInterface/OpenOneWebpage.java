package com.example.kiylx.ti.myInterface;

/**
 * 用于收藏或是历史记录页面，点击历史记录或是收藏记录时打开这个网页
 */
public interface OpenOneWebpage {
    /**
     * @param url 要打开的url名称
     * @param flags true代表用新标签页打开，flase表示直接用当前页面打开
     */
    void loadUrl(String url, boolean flags);
}
