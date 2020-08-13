package com.crystal.customview.baseadapter1;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * 创建者 kiylx
 * 创建时间 2020/4/6 8:47
 */
public abstract class BaseAdapter<T, N extends BaseHolder> extends RecyclerView.Adapter<N> {
    List<T> list;//T类型的bean的list集合

    public BaseAdapter(List<T> list) {
        setData(list);
    }

    public List<T> getData() {
        return list;
    }

    public void setData(List<T> list) {
        this.list = list;
    }

    @Override
    public int getItemViewType(int position) {
        return getViewType(list.get(position));
    }

    @NonNull
    @Override
    public N onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView=LayoutInflater.from(parent.getContext()).inflate(getItemViewResId(viewType),parent,false);
        return createHolder(itemView,viewType);
    }



    @Override
    public void onBindViewHolder(@NonNull N holder, int position) {
        bind(holder, list.get(position),getItemViewType(position));
    }

    @Override
    public int getItemCount() {
        if (list == null) {
            return -1;
        } else
            return list.size();
    }
    /**
     * @param viewType viewtype
     * @return 根据viewType,返回不同的itemView的layoutId
     */
    protected abstract int getItemViewResId(int viewType);
    /**
     * @param bean 数据bean
     * @return 根据bean返回不同的viewtype
     * 默认返回0。根据bean来提供不同的viewtype，用于加载不同itemView生成不同的viewHolder
     */
    public abstract int getViewType(T bean);

    /**
     * 泛型继承自baseholder的holder类
     *
     * @param itemView itemView
     * @param viewType viewType
     * @return 根据ViewType的不同生成不同的viewHolder实例
     */
    public abstract N createHolder(View itemView,int viewType);

    /**
     * @param holder viewHolder
     * @param data 给viewholder的数据bean
     * @param viewType 此itemview的viewtype
     *                 根据viewType将数据绑定在不同的holder上
     */
    public abstract void bind(N holder, T data,int viewType);
}
