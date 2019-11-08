package com.example.kiylx.ti.Fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.kiylx.ti.Core1.AboutBookmark;
import com.example.kiylx.ti.Core1.AboutTag;
import com.example.kiylx.ti.INTERFACE.RefreshBookMark;
import com.example.kiylx.ti.R;

import java.util.Objects;

public class DeleteTag_Dialog extends DialogFragment {
    private static final String TAG = "DeleteTag_Dialog";
    private String tag;
    private AboutTag aboutTag;
    private AboutBookmark aboutBookmark;
    private static final String ARG_PARAM = "param";
    private static RefreshBookMark refresh;


    public static DeleteTag_Dialog getInstance(String tagname){

        Bundle arg=new Bundle();
        arg.putSerializable(ARG_PARAM,tagname);//把要删除的tag拿到

        DeleteTag_Dialog mdialog=new DeleteTag_Dialog();
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
                //删除标签同时把这个标签下的书签记录的tag改成默认的“未分类”
                updateBookmark(tag,"未分类");
                //刷新BookmarkPageActivity里面的视图
                refresh.refresh();

            }
        });
        builder.setNegativeButton("连同书签一起删除", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteAll(tag);
                refresh.refresh();
            }
        });
        //删除tag
        aboutTag.delete(tag);
        return builder.create();

    }

    private void deleteAll(String tag) {
        //把有这个标签的书签尽数删除
        aboutBookmark.deleteBookMarkfromTag(tag);
    }

    private void updateBookmark(String tag,String newTagname) {
        //把有相关标签的书签批量更改它的标签
        aboutBookmark.updateTagsforItems(tag,newTagname);
    }
    public static void setInterface(RefreshBookMark minterface){
        DeleteTag_Dialog.refresh =minterface;
    }

}
