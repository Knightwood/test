package com.crystal.treelistviewdemo;


import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * @创建者 kiylx
 * @创建时间 2020/3/3 21:44
 */
public class TreeViewAdapter extends RecyclerView.Adapter<TreeViewHolder> {

    private List<Node> lists;

    public void setLists(List<Node> lists) {
        this.lists = lists;
    }

    public TreeViewAdapter() {

    }

    @NonNull
    @Override
    public TreeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull TreeViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return lists.size();
    }
}
