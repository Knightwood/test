package com.crystal.customview.floatingwindow;

import android.graphics.Color;
import android.graphics.PixelFormat;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

/**
 * 创建者 kiylx
 * 创建时间 2020/4/16 16:52
 */
public class FloatingViewTest extends FloatingWindowService {
    private static final String TAG = "FloatingViewTest";
    int a = 1;

    @Override
    public View setLayout() {
        final EditText button = new EditText(getApplicationContext());
        button.setText("Floating Window");
        button.setBackgroundColor(Color.BLUE);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: 按钮被点击了");
                if (a > 0){
                    canEdit(true);
                    button.requestFocus();
                }

                a -= 1;
            }
        });
        return button;

        /*View v= LayoutInflater.from(getApplicationContext()).inflate(R.layout.floating,null,false);
        final EditText editText=v.findViewById(R.id.edit12);
        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: 按钮被点击了");
                if (a > 0){
                    canEdit(true);
                    editText.requestFocus();
                }

                a -= 1;
            }
        });
        return v;*/
    }

    @Override
    public WindowManager.LayoutParams setLp() {
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.format = PixelFormat.RGBA_8888;
        layoutParams.width = 500;
        layoutParams.height = 400;
        layoutParams.x = 300;
        layoutParams.y = 300;

        return layoutParams;
    }
}
