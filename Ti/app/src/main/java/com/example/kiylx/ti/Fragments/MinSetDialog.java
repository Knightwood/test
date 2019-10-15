package com.example.kiylx.ti.Fragments;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.kiylx.ti.Activitys.HistoryActivity;
import com.example.kiylx.ti.Activitys.SettingActivity;
import com.example.kiylx.ti.Activitys.BookmarkPageActivity;
import com.example.kiylx.ti.R;

import static android.widget.Toast.LENGTH_SHORT;

public class MinSetDialog extends DialogFragment implements View.OnClickListener {
    /*设置，下载，收藏，历史记录，分享，隐身，工具箱，电脑模式*/

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.min_set_view,null);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        //super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.a1).setOnClickListener(this);
        view.findViewById(R.id.a2).setOnClickListener(this);
        view.findViewById(R.id.a3).setOnClickListener(this);
        view.findViewById(R.id.a4).setOnClickListener(this);
        view.findViewById(R.id.a5).setOnClickListener(this);
        view.findViewById(R.id.a6).setOnClickListener(this);
        view.findViewById(R.id.a7).setOnClickListener(this);
        view.findViewById(R.id.a8).setOnClickListener(this);
        view.findViewById(R.id.a9).setOnClickListener(this);
        view.findViewById(R.id.a10).setOnClickListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if(dialog!=null&&dialog.getWindow()!=null){
            Window window = dialog.getWindow();
            WindowManager.LayoutParams lp=window.getAttributes();
            lp.gravity= Gravity.BOTTOM;
            lp.width=WindowManager.LayoutParams.MATCH_PARENT;
            lp.height=WindowManager.LayoutParams.WRAP_CONTENT;
            window.setBackgroundDrawable(new ColorDrawable(Color.rgb(255,255,255)));
            window.setWindowAnimations(R.style.animate_dialog);
            window.setAttributes(lp);
            //设置点击外部可以取消对话框
            setCancelable(true);
        }
    }
    @Override
    public void onClick(View v){
        v.setSelected(!v.isSelected());
        int id = v.getId();
        switch (id) {
            case R.id.a1:
                //分享
                dismiss();
                break;
            case R.id.a2:
                //收藏
                dismiss();
                break;
            case R.id.a3:
                //隐身
                dismiss();
                Toast.makeText(getActivity(), "SoGO", LENGTH_SHORT).show();
                break;
            case R.id.a4:
                //历史记录
                Intent history_intent = new Intent(getActivity(), HistoryActivity.class);
                startActivity(history_intent);
                dismiss();
                break;
            case R.id.a5:
                //工具箱
                dismiss();
                break;
            case R.id.a6:
                //书签
                Intent Bookmark_intent = new Intent(getContext(), BookmarkPageActivity.class);
                startActivity(Bookmark_intent);
                dismiss();
                Toast.makeText(getActivity(), "QQ", LENGTH_SHORT).show();
                break;
            case R.id.a7:
                //电脑模式
                dismiss();
                break;
            case R.id.a8:
                //下载
                dismiss();
                break;
            case R.id.a9:
                //夜间
                dismiss();
                break;
            case R.id.a10:
                //设置
                startActivity(new Intent(getActivity(), SettingActivity.class));
                dismiss();
                break;
    }
}}
