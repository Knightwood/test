package com.crystal.customview.BaseAdapter1;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * 创建者 kiylx
 * 创建时间 2020/4/6 8:47
 */
public abstract class BaseAdapter<T> extends RecyclerView.Adapter<BaseHolder> {
    List<T> list;

    public BaseAdapter(List<T> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public BaseHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView= LayoutInflater.from(parent.getContext()).inflate(itemResId(),parent,false);
        return new BaseHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseHolder holder, int position) {
        bind(holder, list.get(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    /**
     * @return 返回item的视图
     */

    public abstract int itemResId();

    public abstract void bind(BaseHolder holder, T data);
}
