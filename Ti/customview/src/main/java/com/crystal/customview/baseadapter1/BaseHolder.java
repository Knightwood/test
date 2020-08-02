package com.crystal.customview.baseadapter1;

import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

/**
 * 创建者 kiylx
 * 创建时间 2020/4/6 8:46
 */
public class BaseHolder <N> extends RecyclerView.ViewHolder {
    private View itemView;
    private SparseArray<View> views;
    private N mInfo;//N类型的bean

    public BaseHolder(View itemView) {
        super(itemView);
        this.itemView = itemView;
        views=new SparseArray<>();//暂存itemView中的子view的id和view对象，
    }

    /**
     * @param resId 资源id
     * @param <T> 要从itemView中获取的view的类型
     * @return 返回查找的view
     * 查找itemview中resId 的view。
     * views用来存储itemview中的子view，减少findviewbyid的次数
     */
    public <T extends View> T getView(int resId){
        View v=views.get(resId);
        if (v==null){
            v=itemView.findViewById(resId);
            views.put(resId,v);
        }
        return (T)v;
    }
    public View getItemView(){
        return itemView;
    }
    /**
     * 设置TextView文本
     */
    public BaseHolder setText(int viewId, CharSequence text) {
        TextView tv = getView(viewId);
        tv.setText(text);
        return this;
    }
    /**
     * 设置View的Visibility
     */
    public BaseHolder setViewVisibility(int viewId, int visibility) {
        getView(viewId).setVisibility(visibility);
        return this;
    }

    /**
     * 设置ImageView的资源
     */
    public BaseHolder setImageResource(int viewId, int resourceId) {
        ImageView imageView = getView(viewId);
        imageView.setImageResource(resourceId);
        return this;
    }

    /**
     * 设置条目点击事件
     */
    public void setOnIntemClickListener(View.OnClickListener listener, N info) {
        itemView.setOnClickListener(listener);
        this.mInfo =info;
    }

    /**
     * 设置条目长按事件
     */
    public void setOnIntemLongClickListener(View.OnLongClickListener listener) {
        itemView.setOnLongClickListener(listener);
    }

    public N getInfo() {
        return mInfo;
    }
}
