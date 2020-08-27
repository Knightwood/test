package com.crystal.customview.baseadapter1;

import android.text.method.Touch;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.crystal.customview.tools.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 创建者 kiylx
 * 创建时间 2020/4/6 8:47
 */
public abstract class BaseAdapter<T, N extends BaseHolder> extends RecyclerView.Adapter<N> {
    private List<T> list = null;//T类型的bean的list集合
    private static final String TAG = "BASEADAPTER";

    public BaseAdapter(List<T> list) {
        this.list = list;
    }

    /**
     * @return 返回初始化adapter时传入的数据
     */
    public List<T> getData() {
        return list;
    }

    /**
     * 给adapter设置数据
     * @param list
     */
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
        View itemView = LayoutInflater.from(parent.getContext()).inflate(getItemViewResId(viewType), parent, false);
        LogUtil.d(TAG, "----------onCreateViewHolder  ");
        return createHolder(itemView, viewType);
    }


    @Override
    public void onBindViewHolder(@NonNull N holder, int position) {
        T data = list.get(position);
        bind(holder, data, getItemViewType(position));
        LogUtil.d(TAG, "----------onBindViewHolder  ");
    }

    /**
     * @return 获取list的大小，若list是null，返回0
     */
    @Override
    public int getItemCount() {
        if (list == null)
            return 0;
        else
            return list.size();
    }

    /**
     * @param viewType viewtype
     * @return 根据viewType, 返回不同的itemView的layoutId
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
    public abstract N createHolder(View itemView, int viewType);

    /**
     * @param holder   viewHolder
     * @param data     给viewholder的数据bean
     * @param viewType 此itemview的viewtype
     *                 根据viewType将数据绑定在不同的holder上
     */
    public abstract void bind(N holder, T data, int viewType);

}
