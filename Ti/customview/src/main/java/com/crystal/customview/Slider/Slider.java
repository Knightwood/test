package com.crystal.customview.Slider;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.crystal.customview.R;

/**
 * 创建者 kiylx
 * 创建时间 2020/4/18 11:50
 */
public class Slider extends ConstraintLayout {
    private int messageTime;//通知消息发送的时间间隔

    public Slider(Context context) {
        this(context, null);
    }

    public Slider(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Slider(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.slider,null,false);
        initAttr(context,attrs);
    }

    private void initAttr(Context context, AttributeSet attrs) {

    }


}
