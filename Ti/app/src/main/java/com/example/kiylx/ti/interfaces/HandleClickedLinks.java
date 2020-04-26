package com.example.kiylx.ti.interfaces;

/**
 * 创建者 kiylx
 * 创建时间 2020/3/27 11:01
 *
 * 处理长按事件的接口
 */
public interface HandleClickedLinks {
    void onImgSelected(int x, int y, int type, String extra);

    void onLinkSelected(int x, int y, int type, String extra);

    void onImgLink(int touchX, int touchY, int type, String extra);
}
