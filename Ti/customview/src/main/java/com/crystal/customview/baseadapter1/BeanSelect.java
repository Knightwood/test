package com.crystal.customview.baseadapter1;
/**
 * 创建者 kiylx
 * 创建时间 2020/8/27 21:18
 * packageName：com.crystal.customview.baseadapter1
 * 描述：bean实现此接口，在多选中就可以标志bean对应的itemview有无被选择
 */
public interface BeanSelect {
    /**
     * @return bean是否被选择，若被选择，返回true
     */
    boolean isSelected();

    /**
     * 设置bean的选择状态
     * @param selected
     */
    void setSelected(boolean selected);
}