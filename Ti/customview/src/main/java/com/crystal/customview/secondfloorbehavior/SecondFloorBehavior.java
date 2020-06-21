package com.crystal.customview.secondfloorbehavior;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

/**
 * 创建者 kiylx
 * 创建时间 2020/5/23 21:37
 */
public class SecondFloorBehavior extends CoordinatorLayout.Behavior<View> {
    private boolean layoutChangeListenerAdded = false;
    //寄主
    private ViewGroup mParent;

    public SecondFloorBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(@NonNull CoordinatorLayout parent, @NonNull View child, @NonNull MotionEvent ev) {
        return super.onInterceptTouchEvent(parent, child, ev);
    }

    @Override
    public boolean onLayoutChild(@NonNull CoordinatorLayout parent, @NonNull View child, int layoutDirection) {
        if (mParent == null) {
            mParent = parent;
        }
        if (!layoutChangeListenerAdded) {
            parent.addOnLayoutChangeListener(mOnLayoutChangeListener);
            layoutChangeListenerAdded = true;
        }
        return false;
    }

    private View.OnLayoutChangeListener mOnLayoutChangeListener = new View.OnLayoutChangeListener() {
        /**
         * 在layout方法被调用时会回调，并在里面对它的子View进行重新布局
         * @param v
         * @param left
         * @param top
         * @param right
         * @param bottom
         * @param oldLeft
         * @param oldTop
         * @param oldRight
         * @param oldBottom
         */
        @Override
        public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
            View headerView = getHeaderView();
            View secondFloorView = getSecondFloorView();
            View firstFloorView = getFirstFloorView();

            //获取一楼View的顶部坐标
            int headerBottom = firstFloorView.getTop();
            //HeaderVIew放在一楼的顶部
            int headerTop=headerBottom-headerView.getHeight();
            headerView.layout(headerView.getLeft(),headerTop,headerView.getRight(),headerBottom);

            //二楼放在HeaderView的顶部
            int secondFloorTop=headerTop-secondFloorView.getHeight();
            secondFloorView.layout(secondFloorView.getLeft(),secondFloorTop,secondFloorView.getRight(),headerTop);

        }
    };

    private View getHeaderView() {
        return getChildAt(0, "HeaderView not found! Does your CoordinatorLayout have more than 1 child?");
    }

    private View getSecondFloorView() {
        return getChildAt(1, "SecondFloorView not found! Does your CoordinatorLayout have more than 2 child?");
    }

    private View getFirstFloorView() {
        return getChildAt(2, "FirstFloorView not found! Does your CoordinatorLayout have more than 3 child?");
    }

    private View getChildAt(int index, String exceptionMessage) {
        if (null == mParent) {
            throwException("SecondFloorBehavior not initialized!");
        }
        View child = mParent.getChildAt(index);
        if (null == child) {
            throwException(exceptionMessage);
        }
        return child;
    }

    private void throwException(String message) {
        throw new IllegalStateException(message);
    }
}
