package com.crystal.customview;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;

import androidx.annotation.Nullable;

/**
 * 创建者 kiylx
 * 创建时间 2020/4/3 11:08
 */
public class numberPicker extends LinearLayout implements View.OnClickListener, TextWatcher {
    private int maxValue;
    private int minValue;
    private int limitValue;
    private int textSize=16;

    private EditText numText;
    private ImageView addView;
    private ImageView subView;

    // 监听事件(负责警戒值回调)
    private OnClickInputListener onClickInputListener;
    // 监听输入框内容变化
    private OnInputNumberListener onInputNumberListener;


    public numberPicker(Context context) {
        super(context);
    }

    public numberPicker(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }
//onclick用于监听“加，减”视图的点击
    @Override
    public void onClick(View v) {

    }

    //textWatcher,用于监听文本框的输入
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {

    }


    public void setOnClickInputListener(OnClickInputListener mOnWarnListener) {
        this.onClickInputListener = mOnWarnListener;
    }

    public void setOnInputNumberListener(OnInputNumberListener onInputNumberListener) {
        this.onInputNumberListener = onInputNumberListener;
    }

    /**
     * 超过警戒值回调
     */
    public interface OnClickInputListener {
        void onWarningForInventory(int inventory);

        void onWarningMinInput(int minValue);

        void onWarningMaxInput(int maxValue);
    }

    /**
     * 输入框数字内容监听
     */
    public interface OnInputNumberListener {

        void beforeTextChanged(CharSequence s, int start, int count, int after);

        void onTextChanged(CharSequence charSequence, int start, int before, int count);

        void afterTextChanged(Editable editable);
    }

}
