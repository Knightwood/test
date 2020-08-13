package com.example.kiylx.ti.ui.fragments;

import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.kiylx.ti.R;

/**
 * A simple {@link Fragment} subclass.
 * 用于选择书签文件夹
 */
public class SelectBookmarkFolderFragment extends DialogFragment {

    public SelectBookmarkFolderFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_select_bookmark_folder, container, false);
    }
}
