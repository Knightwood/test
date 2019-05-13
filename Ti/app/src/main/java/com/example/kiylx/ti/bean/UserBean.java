package com.example.kiylx.ti.bean;

public class UserBean {
    private String name; //网页标题
    private int cint; //网页数量

    public UserBean(String name, int cint) {
        this.name = name;
        this.cint = cint;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCint() {
        return cint;
    }

    public void setCint(int cint) {
        this.cint = cint;
    }
}
