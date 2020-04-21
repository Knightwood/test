package com.example.kiylx.ti.corebase;

import com.example.kiylx.ti.tool.Action;

/**
 * WebViewManager发出更新时，有添加，删除，和更新内容三种行为。
 * 通知订阅者时，用此类封装webpageinfo，让订阅者知道做出什么动作
 */
public class SealedWebPageInfo {

    private WebPage_Info info;
    private int pos;
    private Action action;

    /**
     * @param info   WebPageInfo对象，被封装的基础
     * @param pos    info在ArrayList中的位置
     * @param action 需要执行的动作，添加，删除或是更新信息
     */
    public SealedWebPageInfo(WebPage_Info info, int pos, Action action) {
        this.action = action;
        this.info = info;
        this.pos = pos;
    }

    public WebPage_Info getInfo() {
        return info;
    }

    public void setInfo(WebPage_Info info) {
        this.info = info;
    }

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }
}
