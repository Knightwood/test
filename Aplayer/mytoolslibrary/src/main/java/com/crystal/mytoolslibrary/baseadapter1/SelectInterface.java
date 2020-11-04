package com.crystal.customview.baseadapter1;

/**
 * 创建者 kiylx
 * 创建时间 2020/8/27 21:18
 * packageName：com.crystal.customview.baseadapter1
 * 描述：
 */
public interface SelectInterface {
    /**
     * @return item是否被选择，若被选择，返回true
     */
    boolean isSelected();

    /**
     * 设置item的选择状态
     * @param selected
     */
    void setSelected(boolean selected);
}
