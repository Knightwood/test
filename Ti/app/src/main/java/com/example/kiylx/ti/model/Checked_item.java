package com.example.kiylx.ti.model;

import androidx.annotation.NonNull;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.example.kiylx.ti.interfaces.EditTextInterface;

/**
 * @创建者 kiylx
 * @创建时间 2020/2/21 18:28
 */
public class Checked_item extends BaseObservable {

    @Bindable
    public Boolean checked;
    @Bindable
    private EditTextInterface minterface;

    public Checked_item(@NonNull EditTextInterface editTextInterface, boolean b) {
        this.minterface = editTextInterface;
        this.checked = b;
    }

    public void setChecked(Boolean b) {
        this.checked = b;
        notifyChange();
    }


    public void changeselect(String s) {
        minterface.changeSelect(s);
    }

    public void editText(String s) {
        minterface.editText(s);
    }

    public void deleteItem(String s) {
        minterface.deleteItem(s);
    }
}
