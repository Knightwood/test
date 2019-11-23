package com.example.kiylx.ti.Fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.kiylx.ti.Corebase.DownloadInfo;
import com.example.kiylx.ti.R;

import java.util.Objects;

public class DownloadWindow extends DialogFragment {

    private View downloadWindow;
    private DownloadInfo mDownloadInfo;

    private TextView fileNameView;
    private TextView fileUrlView;

    private DownloadWindow(DownloadInfo info) {
        this.mDownloadInfo = info;
    }

    public DownloadWindow getInstance(DownloadInfo info) {

        return new DownloadWindow(info);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));

        builder.setView(downloadWindow)
                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        //设置界面信息
        setInfo(null);

        return builder.create();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        downloadWindow = getLayoutInflater().inflate(R.layout.downloadwindow, null);
        fileNameView = this.downloadWindow.findViewById(R.id.filename_d);
        fileUrlView = this.downloadWindow.findViewById(R.id.fileurl_d);

        fileNameView.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                fileNameView.removeTextChangedListener(this);
                setInfo(s.toString());
                fileNameView.addTextChangedListener(this);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null && dialog.getWindow() != null) {
            Window window = dialog.getWindow();
            WindowManager.LayoutParams layoutParams = window.getAttributes();
            //指定显示位置
            layoutParams.gravity = Gravity.BOTTOM;
            //指定显示大小
            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            //设置背景，不然无法扩展到屏幕边缘
            window.setBackgroundDrawable(new ColorDrawable(Color.rgb(255, 255, 255)));
            //显示消失动画
            window.setWindowAnimations(R.style.animate_dialog);
            //让属性设置生效
            window.setAttributes(layoutParams);
            //设置点击外部可以取消对话框
            setCancelable(true);
        }
    }

    private void setInfo(String fileName) {

        if (mDownloadInfo == null) {
            //处理错误
        }
        fileNameView.setText(fileName == null ? fileName : mDownloadInfo.getFileName());

        fileUrlView.setText(mDownloadInfo.getUrl());
    }
}