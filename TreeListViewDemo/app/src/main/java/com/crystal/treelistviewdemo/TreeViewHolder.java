package com.crystal.treelistviewdemo;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.crystal.treelistviewdemo.databinding.FileitemBinding;
import com.crystal.treelistviewdemo.databinding.FolderitemBinding;

/**
 * @创建者 kiylx
 * @创建时间 2020/3/3 21:52
 */
public class TreeViewHolder extends RecyclerView.ViewHolder {
    FolderitemBinding folderitemBinding;
    FileitemBinding fileitemBinding;


    public TreeViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public TreeViewHolder(@NonNull FolderitemBinding folderitemBinding) {
        super(folderitemBinding.getRoot());
        this.folderitemBinding = folderitemBinding;
    }

    public TreeViewHolder(@NonNull FileitemBinding fileitemBinding) {
        super(fileitemBinding.getRoot());
        this.fileitemBinding = fileitemBinding;
    }


    public void bindfolder(Node node) {
        this.folderitemBinding.setFolderinfo(new NodeBean(node.getName(),node.getId(),node.getParentId(),node.getIcon(),node.isExpand(),node.getLevel()));
    }

    public void bindfile(Node node) {
        this.fileitemBinding.setFileinfo(new NodeBean(node.getName(),node.getId(),node.getParentId(),node.getIcon(),node.isExpand(),node.getLevel()));
    }
}
