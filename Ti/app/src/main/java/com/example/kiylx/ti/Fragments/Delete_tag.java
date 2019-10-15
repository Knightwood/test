package com.example.kiylx.ti.Fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.example.kiylx.ti.AboutStar;
import com.example.kiylx.ti.AboutTag;
import com.example.kiylx.ti.R;

import java.util.Objects;

public class Delete_tag extends DialogFragment {
    private static final String TAG = "Delete_tag";
    private String tag;
    private AboutTag aboutTag;
    private AboutStar aboutStar;


    public static Delete_tag getInstance(String tagname){
        Bundle arg=new Bundle();
        arg.putSerializable("target_tag",tagname);//把要删除的tag拿到

        Delete_tag mdialog=new Delete_tag();
        mdialog.setArguments(arg);
        return mdialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        aboutTag=AboutTag.get(getActivity());
        aboutStar=AboutStar.get(getActivity());
        if(getArguments() !=null){
            tag=getArguments().getString("删除标签dialog");
            Log.d(TAG,"要删除的标签"+tag);
        }

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder=new AlertDialog.Builder(Objects.requireNonNull(getActivity()));
        final View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_deletetag,null);
        builder.setView(view);
        builder.setPositiveButton("仅删除标签", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                updateStar(tag);

            }
        });
        builder.setNegativeButton("连同书签一起删除", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteAll(tag);
            }
        });
        aboutTag.delete(tag);
        return builder.create();

    }

    private void deleteAll(String tag) {
        aboutStar.deleteWithtag(tag);
    }

    private void updateStar(String tag) {
        //把有相关tag的书签批量更改
        aboutStar.changeTags(tag);
    }

    @Override
    public void setTargetFragment(@Nullable Fragment fragment, int requestCode) {
        super.setTargetFragment(fragment, requestCode);
    }
}
