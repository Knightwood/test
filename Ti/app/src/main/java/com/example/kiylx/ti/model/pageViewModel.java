package com.example.kiylx.ti.model;

/**
 * 用于多窗口更新文字，使用的databinding
 */
public class pageViewModel {
    private String title;
    private String Url;
    private int pos;

    public pageViewModel(String title,String Url,int pos){
        this.title=title;
        this.pos=pos;
        this.Url=Url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return Url;
    }

    public void setUrl(String url) {
        Url = url;
    }

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }
}
