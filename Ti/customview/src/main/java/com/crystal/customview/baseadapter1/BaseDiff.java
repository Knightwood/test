package com.crystal.customview.baseadapter1;

import androidx.recyclerview.widget.DiffUtil;

/**
 * 创建者 kiylx
 * 创建时间 2020/8/12 21:24
 * packageName：com.crystal.customview.baseadapter1
 * 描述：
 */
public class BaseDiff extends DiffUtil.Callback {
    @Override
    public int getOldListSize() {
        return 0;
    }

    @Override
    public int getNewListSize() {
        return 0;
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return false;
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return false;
    }
}
