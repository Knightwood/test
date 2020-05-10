package com.example.kiylx.ti.ui.fragments;

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

import com.example.kiylx.ti.managercore.AboutBookmark;
import com.example.kiylx.ti.managercore.BookMarkFolderManager;
import com.example.kiylx.ti.conf.SomeRes;
import com.example.kiylx.ti.interfaces.RefreshBookMark;
import com.example.kiylx.ti.R;

import java.util.Objects;

public class DeleteBookmarkFolder_Dialog extends DialogFragment {
    private static final String TAG = "删除书签文件夹";
    private String folderName;
    private BookMarkFolderManager bookMarkFolderManager;
    private AboutBookmark aboutBookmark;
    private static final String ARG_PARAM = "param";
    private static RefreshBookMark refresh;


    public static DeleteBookmarkFolder_Dialog getInstance(String tagname){

        Bundle arg=new Bundle();
        arg.putSerializable(ARG_PARAM,tagname);//把要删除的tag拿到

        DeleteBookmarkFolder_Dialog mdialog=new DeleteBookmarkFolder_Dialog();
        mdialog.setArguments(arg);
        return mdialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bookMarkFolderManager = BookMarkFolderManager.get(getActivity());
        aboutBookmark=AboutBookmark.get(getActivity());
        if(getArguments() !=null){
            folderName =getArguments().getString(ARG_PARAM);
            Log.d(TAG, String.format("要删除的标签%s", folderName));
        }

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder=new AlertDialog.Builder(requireActivity());
        final View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_deletetag,null);
        builder.setView(view);
        builder.setPositiveButton("仅删除标签", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //把这个标签下的书签记录的文件夹改成默认的“未分类”
                updateBookmark(folderName, SomeRes.defaultBookmarkFolder);
                //刷新BookmarkPageActivity里面的视图
                refresh.refresh();

            }
        });
        builder.setNegativeButton("连同书签一起删除", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteAll(folderName);
                refresh.refresh();
            }
        });
        //删除书签文件夹
        bookMarkFolderManager.delete(folderName);
        return builder.create();
    }

    private void deleteAll(String folder) {
        //把所属这个文件夹的书签尽数删除
        aboutBookmark.deleteBookMarkWithFolderName(folder);
    }

    private void updateBookmark(String folderName,String newFoldername) {
        //把有相关标签的书签批量更改它的文件夹
        aboutBookmark.updateFolderforItems(folderName,newFoldername);
    }
    public static void setInterface(RefreshBookMark minterface){
        DeleteBookmarkFolder_Dialog.refresh =minterface;
    }

}
