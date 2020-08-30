package com.crystal.customview.baseadapter1;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * 创建者 kiylx
 * 创建时间 2020/8/28 18:38
 * packageName：com.crystal.customview.baseadapter1
 * 描述：可以多选itemView的adapter
 */
public abstract class MultiSelectAdapter<T extends BeanSelect, N extends BaseHolder> extends BaseAdapter<T, N> {
    private List<T> beSelectItems = null;//被选择的bean
    private boolean selecting;//是否开始选择,true则adapter进入选择模式
    private int selectNum = 0;//可以选择的item的数量，等于0则无限制
    private int selectAllNum=1;//全选时将此计数减1，避免重复调用selectAll()

    public MultiSelectAdapter(List<T> list) {
        super(list);
        beSelectItems = new ArrayList<>();
    }

    /**
     * @return 如果adapter在选择模式，返回true
     */
    public boolean isSelectingMode() {
        return selecting;
    }

    /**
     * @param selecting 是否让adapter进入选择模式
     */
    public void changeSelectMode(boolean selecting) {
        this.selecting = selecting;
        for (T t : beSelectItems) {
            t.setSelected(false);
        }
        beSelectItems.clear();
        notifyDataSetChanged();
    }

    /**
     * @param bean   被选择的bean
     * @param select 被选择传入true，将bean加入被选择list中，传入false，则将bean从被选择list中删除
     *               调用此方法会无视selectNum的限制
     */
    public void setItemSelect(T bean, boolean select) {

        bean.setSelected(select);
        if (select) {
            beSelectItems.add(bean);
        } else {
            beSelectItems.remove(bean);
        }
        notifyItemChanged(list.indexOf(bean));
    }

    /**
     * @param bean 根据数量限制，以及被选择或反选，将bean加入list或从list中删除
     */
    public void setItemSelect(T bean) {
        if (selectNum == 0 || selectNum > beSelectItems.size()) {
            bean.setSelected(!bean.isSelected());
        } else {
            return;
        }
        if (beSelectItems.contains(bean)) {
            beSelectItems.remove(bean);
        } else {
            beSelectItems.add(bean);

        }
        notifyItemChanged(list.indexOf(bean));
    }

    /**
     * @param bean 验证此bean有无被选择
     * @return 被选择，返回true
     */
    public boolean itemIsSelect(T bean) {
        return bean.isSelected();
    }

    /**
     * @return 返回被选择的bean的集合
     */
    public List<T> getBeSelectItems() {
        return beSelectItems;
    }

    public int getSelectNum() {
        return selectNum;
    }

    public void setSelectNum(int selectNum) {
        this.selectNum = selectNum;
    }

    /**
     * @param b true是被选择，false是不选择
     *          让所有数据被选择或是取消选择
     */
    public void selectAll(boolean b) {
        if (selectAllNum>0){
            for (T data : list) {
                data.setSelected(b);
            }
            beSelectItems.clear();
            beSelectItems.addAll(list);
            notifyDataSetChanged();
            selectAllNum-=1;
        }

    }

    /**
     * 反选，让被选择的数据取消选择，没被选择的数据被选择
     */
    public void ReverseSelect(){
        if (!beSelectItems.isEmpty()) {
            for (T data :beSelectItems) {
                data.setSelected(!data.isSelected());
            }
            beSelectItems.clear();
            notifyDataSetChanged();
        }
    }

    /**
     * 不使用多选时复原这些变量
     */
    public void destroy() {
        changeSelectMode(false);
        beSelectItems.clear();
        selectAllNum=1;
        selectNum=0;
    }
}
