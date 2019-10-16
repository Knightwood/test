package com.example.kiylx.ti.Fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.example.kiylx.ti.AboutBookmark;
import com.example.kiylx.ti.AboutTag;
import com.example.kiylx.ti.R;

import java.util.Objects;

public class Delete_tag extends DialogFragment {
    private static final String TAG = "Delete_tag";
    private String tag;
    private AboutTag aboutTag;
    private AboutBookmark aboutBookmark;
    private static final String ARG_PARAM = "param";
    private static Delete_tagF_interface mDelete_tag;


    public static Delete_tag getInstance(String tagname){
        Bundle arg=new Bundle();
        arg.putSerializable(ARG_PARAM,tagname);//把要删除的tag拿到

        Delete_tag mdialog=new Delete_tag();
        mdialog.setArguments(arg);
        return mdialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        aboutTag=AboutTag.get(getActivity());
        aboutBookmark=AboutBookmark.get(getActivity());
        if(getArguments() !=null){
            tag=getArguments().getString(ARG_PARAM);
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
                updateBookmark(tag);

            }
        });
        builder.setNegativeButton("连同书签一起删除", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteAll(tag);
            }
        });
        //删除tag
        aboutTag.delete(tag);
        //调用onActivityresult刷新BookmarkPageActivity里面的视图
        mDelete_tag.onResult();
        return builder.create();

    }

    private void deleteAll(String tag) {
        aboutBookmark.deleteWithtag(tag);
    }

    private void updateBookmark(String tag) {
        //把有相关tag的书签批量更改
        aboutBookmark.changeTags(tag);
    }
    public static void setInterface(Delete_tagF_interface minterface){
        Delete_tag.mDelete_tag=minterface;
    }
    public interface Delete_tagF_interface{
        void onResult();
    }
}
