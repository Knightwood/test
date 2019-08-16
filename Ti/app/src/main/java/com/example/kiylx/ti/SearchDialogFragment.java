package com.example.kiylx.ti;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.SearchView;

public class SearchDialogFragment extends DialogFragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_bottom_dialog,null);
        SearchView searchView = v.findViewById(R.id.real_search);
        searchView.setIconified(false);

        return v;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }


    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null && dialog.getWindow() != null) {
    Window window = dialog.getWindow();
    //指定显示位置
            dialog.getWindow().setGravity(Gravity.BOTTOM);
    //指定显示大小
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    //显示消失动画
            window.setWindowAnimations(R.style.animate_dialog);
    //设置背景透明
           // window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    //new ColorDrawable(Color.TRANSPARENT),设置为null可以使其不透明，设置为transparent可以使其透明


    //设置点击外部可以取消对话框
    setCancelable(true);
        }
    }
}
