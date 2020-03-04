package com.crystal.treelistviewdemo;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

/**
 * 创建者 kiylx
 * 创建时间 2020/3/3 21:59
 */
public class NodeBean extends BaseObservable {
    private int level;
    private String name;
    private int id;
    private int pid;
    private int icon;
    private boolean isExpand;

    public NodeBean(String name, int id, int pid, int icon, boolean isExpand, int level) {
        this.name = name;
        this.id = id;
        this.pid = pid;
        this.icon = icon;
        this.isExpand = isExpand;
        this.level = level;
    }


    @Bindable
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        notify();
    }

    @Bindable
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
        notify();
    }

    @Bindable
    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
        notify();
    }

    @Bindable
    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
        notify();
    }

    @Bindable
    public boolean isExpand() {
        return isExpand;
    }

    public void setExpand(boolean expand) {
        isExpand = expand;
        notify();
    }

    @Bindable
    public String getLevel() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < level; i++) {
            builder.append("  ");
        }
        return builder.toString();
    }

    public void setLevel(int level) {
        this.level = level;
        notify();
    }

}
