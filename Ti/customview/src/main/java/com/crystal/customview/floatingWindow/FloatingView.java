package com.crystal.customview.floatingWindow;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

/**
 * 创建者 kiylx
 * 创建时间 2020/4/16 11:27
 */
public class FloatingView extends LinearLayout {
    public FloatingView(Context context) {
        this(context,null);
    }

    public FloatingView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public FloatingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttr();
    }

    private void initAttr() {

    }
}
