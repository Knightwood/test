package com.crystal.treelistviewdemo;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.crystal.treelistviewdemo.databinding.FileitemBinding;
import com.crystal.treelistviewdemo.databinding.FolderitemBinding;

import java.util.List;

/**
 * @创建者 kiylx
 * @创建时间 2020/3/3 21:44
 */
public class TreeViewAdapter extends RecyclerView.Adapter<TreeViewHolder> {

    private List<Node> lists;
    private LayoutInflater minflate;

    public void setLists(List<Node> lists) {
        this.lists = lists;
        notifyDataSetChanged();
    }

    public TreeViewAdapter(List<Node> lists, Context context) {
        this.lists = lists;
        minflate = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public TreeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        FileitemBinding fileitemBinding = DataBindingUtil.inflate(minflate, R.layout.fileitem, parent, false);
        FolderitemBinding folderitemBinding = DataBindingUtil.inflate(minflate, R.layout.folderitem, parent, false);
        if (viewType == 0) {
            return new TreeViewHolder(folderitemBinding);
        } else {
            return new TreeViewHolder(fileitemBinding);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull TreeViewHolder holder, int position) {
        if (getItemViewType(position) == 0) {
            holder.bindfolder(lists.get(position));
        } else {
            holder.bindfile(lists.get(position));
        }

    }

    @Override
    public int getItemCount() {
        return lists.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (lists.get(position).isFolder()) {
            return 0;
        } else {
            return 1;
        }

    }
}
