package com.crystal.customview.baseadapter1;

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
public abstract class BaseAdapter<T,N extends BaseHolder> extends RecyclerView.Adapter<N> {
    List<T> list;

    public BaseAdapter(List<T> list) {
        this.list = list;
    }
    public void setData(List<T> list){
        this.list=list;
    }

    @NonNull
    @Override
    public N onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView= LayoutInflater.from(parent.getContext()).inflate(itemResId(),parent,false);
        return createHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull N holder, int position) {
        bind(holder, list.get(position));
    }

    @Override
    public int getItemCount() {
        if (list==null){
            return -1;
        }else
        return list.size();
    }

    /**
     * @param v
     * @return 继承自baseholder的holder
     */
    public abstract N createHolder(View v);

    /**
     * @return 返回item的视图
     */

    public abstract int itemResId();

    public abstract void bind(N holder, T data);
}
