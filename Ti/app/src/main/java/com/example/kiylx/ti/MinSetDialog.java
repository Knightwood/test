package com.example.kiylx.ti;

import android.app.Dialog;
import android.content.Intent;
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
import android.view.WindowManager;
import android.widget.Toast;

import static android.widget.Toast.LENGTH_SHORT;

public class MinSetDialog extends DialogFragment implements View.OnClickListener {
    /*设置，下载，收藏，历史记录，分享，隐身，工具箱，电脑模式*/

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.min_set_dialog,null);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        //super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.sogo1).setOnClickListener(this);
        view.findViewById(R.id.sogo2).setOnClickListener(this);
        view.findViewById(R.id.sogo3).setOnClickListener(this);
        view.findViewById(R.id.qq1).setOnClickListener(this);
        view.findViewById(R.id.qq2).setOnClickListener(this);
        view.findViewById(R.id.qq3).setOnClickListener(this);
        view.findViewById(R.id.ie1).setOnClickListener(this);
        view.findViewById(R.id.ie2).setOnClickListener(this);
        view.findViewById(R.id.menu).setOnClickListener(this);
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
            window.setBackgroundDrawable(new ColorDrawable(Color.rgb(91,90,92)));
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
            case R.id.sogo1:
            case R.id.sogo2:
            case R.id.sogo3:
                Toast.makeText(getActivity(), "SoGO", LENGTH_SHORT).show();
                break;
            case R.id.qq1:
            case R.id.qq2:
            case R.id.qq3:
                Toast.makeText(getActivity(), "QQ", LENGTH_SHORT).show();
                break;
            case R.id.ie1:
                break;
            case R.id.ie2:
                break;
            case R.id.menu:
                startActivity(new Intent(getActivity(),SettingActivity.class));
                break;
    }
}}
