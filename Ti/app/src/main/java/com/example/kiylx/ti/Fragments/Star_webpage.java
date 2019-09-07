package com.example.kiylx.ti.Fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.example.kiylx.ti.R;
import com.example.kiylx.ti.model.WebPage_Info;

public class Star_webpage extends DialogFragment {
    GetInfo mGetInfo;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGetInfo=(GetInfo)getActivity();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        //onCreateDialog在onCreate之后，onCreateView之前被调用
        //return super.onCreateDialog(savedInstanceState);
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.star_webpage_dialog,null);
        setMassage(view);
        builder.setView(view)
                .setPositiveButton(R.string.enter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        return builder.create();

    }
    public interface GetInfo{
        //获取当前网页信息以填充收藏窗口
        public WebPage_Info getInfo();
    }

    private void setMassage(View v) {
        WebPage_Info info = mGetInfo.getInfo();
        EditText title=v.findViewById(R.id.edit_title);
        title.setText(info.getTitle());
        EditText url=v.findViewById(R.id.editUrl);
        url.setText(info.getUrl());
        TextView tags=v.findViewById(R.id.editfolders);

    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
