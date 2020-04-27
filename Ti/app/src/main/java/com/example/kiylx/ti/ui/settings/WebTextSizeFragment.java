package com.example.kiylx.ti.ui.settings;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.kiylx.ti.R;

/**
 * 创建者 kiylx
 * 创建时间 2020/4/27 10:47
 */
public class WebTextSizeFragment extends DialogFragment {
    public WebTextSizeFragment newInstance(){
        return new WebTextSizeFragment();
    }
    public WebTextSizeFragment(){
        super();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.webtextsize_view,container,false);

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null && dialog.getWindow() != null) {
            Window window = dialog.getWindow();
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.MATCH_PARENT;
            window.setBackgroundDrawable(new ColorDrawable(Color.rgb(255, 255, 255)));
            window.setWindowAnimations(R.style.animate_dialog);
            window.setAttributes(lp);
            //设置点击外部可以取消对话框
            setCancelable(true);
        }
    }
}
