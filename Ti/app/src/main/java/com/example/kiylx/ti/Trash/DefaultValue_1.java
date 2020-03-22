package com.example.kiylx.ti.Trash;

/**
 * @创建者 kiylx
 * @创建时间 2020/2/27 15:46
 *
 * 这是关于webview的一些设置，livedata通过这个进行推送
 */
public class DefaultValue_1 {
    private String user_agent;
    private Boolean useCustomDwnloadTool;
    private int textZoom;

    public DefaultValue_1(String s){
        this.user_agent=s;
    }


    public String getUser_agent() {
        return user_agent;
    }

    public void setUser_agent(String user_agent) {
        this.user_agent = user_agent;

    }

    public Boolean getUseCustomDwnloadTool() {
        return useCustomDwnloadTool;
    }

    public void setUseCustomDwnloadTool(Boolean useCustomDwnloadTool) {
        this.useCustomDwnloadTool = useCustomDwnloadTool;
    }

    public int getTextZoom() {
        return textZoom;
    }

    public void setTextZoom(int textZoom) {
        this.textZoom = textZoom;
    }
}
