package com.example.kiylx.ti.model;

import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.databinding.Bindable;

import com.example.kiylx.ti.myInterface.EditTextInterface;

/**
 * @创建者 kiylx
 * @创建时间 2020/2/21 18:28
 */
public class Checked_item {

    public Boolean checked;
    private EditTextInterface minterface;

    public Checked_item(@NonNull EditTextInterface editTextInterface, boolean b) {
        this.minterface=editTextInterface;
        this.checked = b;
    }

    public void setChecked(Boolean b) {
        this.checked = b;
    }

    public void change() {
        this.checked = !this.checked;
    }
    public void changeselect(String s){
        minterface.changeSelect(s);
    }
    public void editText(String s){
        minterface.editText(s);
    }
    public void deleteItem(String s){
        minterface.deleteItem(s);
    }
}
