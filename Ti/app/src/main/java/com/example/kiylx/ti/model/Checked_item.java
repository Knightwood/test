package com.example.kiylx.ti.model;

import androidx.databinding.Bindable;

/**
 * @创建者 kiylx
 * @创建时间 2020/2/21 18:28
 */
public class Checked_item {

    public Boolean checked;

    public Checked_item(boolean b) {
        this.checked = b;
    }

    public void setChecked(Boolean b) {
        this.checked = b;
    }

    public void change() {
        this.checked = !this.checked;
    }
}
