package com.crystal.customview.floatingWindow;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

/**
 * 创建者 kiylx
 * 创建时间 2020/4/16 16:52
 */
public class floatTest extends FloatingWindowService {
    private static final String TAG="floatTest";

    @Override
    public View setLayout() {
        Button button = new Button(getApplicationContext());
        button.setText("Floating Window");
        button.setBackgroundColor(Color.BLUE);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: 按钮被点击了");
            }
        });
        return button;
    }

    @Override
    public WindowManager.LayoutParams setLp() {
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.format = PixelFormat.RGBA_8888;
        layoutParams.width = 500;
        layoutParams.height = 100;
        layoutParams.x = 300;
        layoutParams.y = 300;

        return layoutParams;
    }
}
