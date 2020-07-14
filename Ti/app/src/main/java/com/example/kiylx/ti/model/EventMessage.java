package com.example.kiylx.ti.model;

/**
 * 创建者 kiylx
 * 创建时间 2020/3/26 15:44
 *
 * type: 1; message:"更新下载列表" 发送源：DownloadManager 接收方：downloadingFragment
 * type: 2: message:"更新在view上网页的标题"发送源：WebViewManager 接收方：multPage_Dialog,MainActivity //在mainActivity下面和多窗口部分的网址或标题的更新
 * type: 3: message: 销毁trashList中的垃圾
 */
public class EventMessage {
    private int type;
    private String message;

    public EventMessage(int type, String message) {
        this.type = type;
        this.message = message;
    }

    @Override
    public String toString() {

        return "type="+type+"--message= "+message;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
