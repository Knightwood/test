package com.example.kiylx.ti.trash;

import android.content.Context;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.customview.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

public class minsetViewgroup extends ViewGroup {
    private ViewDragHelper mDragger;
    private View topView, bottomView;
    private openChangeListener mOpenChangeListener;
    private int elevationHeight = 25;//层叠高度
    private int extendHeight = 30;//延伸高度
    private boolean isExpand = true;

    public minsetViewgroup(Context context) {
        super(context);
    }

    public minsetViewgroup(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public minsetViewgroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public minsetViewgroup(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode =MeasureSpec.getMode(heightMeasureSpec);
        int sizeWidth =MeasureSpec.getSize(widthMeasureSpec);
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);
// 计算出所有的childView的宽和高
        measureChildren(widthMeasureSpec,heightMeasureSpec);

        int width = 0;
        int height = 0;
        for(int i=0;i<getChildCount();i++){
            View childView = getChildAt(i);
            MarginLayoutParams cParams =(MarginLayoutParams) childView.getLayoutParams();

            int cWidthWithMargin = childView.getMeasuredWidth()+cParams.leftMargin+cParams.rightMargin;
            int cHeightWithMargin = childView.getMeasuredHeight()+cParams.topMargin+cParams.bottomMargin;

            //高度为两个子view的和
            height = height+cHeightWithMargin;
            //宽度取两个子view中的最大值
            width =cWidthWithMargin>width?cWidthWithMargin:width;

        }
        setMeasuredDimension((widthMode ==MeasureSpec.EXACTLY)?sizeWidth:width,(heightMode==MeasureSpec.EXACTLY)?sizeHeight:height);

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        for(int i=0;i<getChildCount();i++){
            View childView = getChildAt(i);
            int cWidth = childView.getMeasuredWidth();
            int cHeight = childView.getMeasuredHeight();
            MarginLayoutParams cParmas = (MarginLayoutParams) childView.getLayoutParams();
             int cl=0,ct=0,cr=0,cb=0;
             switch (i){
                 case 0://bottomView放下面
                     cl=cParmas.leftMargin;
                     ct=getHeight()-cHeight-cParmas.bottomMargin;
                     cb=cHeight+ct;
                     childView.setPadding(0, extendHeight, 0, 0);
                     cr=cl+cWidth;
                     break;
                 case 1://topView放上面
                     cl=cParmas.leftMargin;
                     ct=cParmas.topMargin;
                     cb=cHeight+ct;
                     cr=cl+cWidth;
                     break;

             }
             childView.layout(cl,ct,cr,cb);
        }

    }
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (getChildCount() != 2) {
            throw new RuntimeException("必须是2个子View！");
        }
        if(mDragger==null){
        topView = getChildAt(0);
        bottomView = getChildAt(1);
        bringChildToFront(topView);
        mDragger=ViewDragHelper.create(this,1.0f,new ViewDragHelperCallBack());
    }}
    //折叠布局
    public void foldLayout() {
        if (topView != null && mDragger != null) {
            isExpand = false;
            mDragger.settleCapturedViewAt(topView.getLeft(), getHeight() - topView.getHeight() - elevationHeight);
        }
    }

    //还原/展开布局
    public void expandLayout() {
        if (topView != null && mDragger != null) {
            mDragger.settleCapturedViewAt(topView.getLeft(), 0);
            isExpand = true;
        }
    }

    //布局是否展开状态
    public boolean isExpand() {
        return isExpand;
    }

    @Override
    public void computeScroll() {
        if (mDragger.continueSettling(true)) {
            invalidate();
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return mDragger.shouldInterceptTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mDragger.processTouchEvent(event);
        return true;
    }
    private class ViewDragHelperCallBack extends ViewDragHelper.Callback{

        @Override
        public boolean tryCaptureView(@NonNull View view, int i) {
            /*
    参数1：捕获的View（也就是你拖动的这个View）
    参数2：这个参数我也不知道什么意思API中写的一个什么指针，这里没有到也没有注意
*/
            return topView==view;//限制只有topView可以滑动
        }

        @Override
        public int clampViewPositionHorizontal(@NonNull View child, int left, int dx) {
            //return super.clampViewPositionHorizontal(child, left, dx);
            return 0;//横向可滑动范围，因为不可以横向滑动直接返回0就行
        }

        @Override
        public int getViewVerticalDragRange(@NonNull View child) {
            return getMeasuredHeight()-child.getMeasuredHeight();
        }

        @Override
        public int clampViewPositionVertical(@NonNull View child, int top, int dy) {
            //return super.clampViewPositionVertical(child, top, dy);
            //竖向可滑动范围，top是child即将滑动到的top值，限制top的范围在topBound和bottomBound之间。
            final int topBound = getPaddingTop();
            final int bottomBound = getHeight()-child.getHeight()-getPaddingBottom();
            return Math.min(Math.max(top,topBound),bottomBound);
        }

        @Override
        public void onViewPositionChanged(@NonNull View changedView, int left, int top, int dx, int dy) {
            //super.onViewPositionChanged(changedView, left, top, dx, dy);
            float percent = (float) top/(getHeight()-changedView.getHeight());
            //处理topView动画
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                changedView.setElevation(percent * 10);
            }
            //处理bottomView动画
            bottomView.setScaleX(1 - percent * 0.03f);
        }

        @Override
        public void onViewReleased(@NonNull View releasedChild, float xvel, float yvel) {
            //手指释放时,滑动距离大于一半直接滚动到底部，否则返回顶部
            if (releasedChild == topView) {
                float movePercentage = (float) (releasedChild.getTop()) / (getHeight() - releasedChild.getHeight() - elevationHeight);
                int finalTop = (movePercentage >= .5f) ? getHeight() - releasedChild.getHeight() - elevationHeight : 0;
                mDragger.settleCapturedViewAt(releasedChild.getLeft(), finalTop);
                invalidate();
        }
    }
}
    public void setExtendHeight(int extendHeight) {
        this.extendHeight = extendHeight;
        requestLayout();
    }

    public void setOpenChangeListener(openChangeListener openChangeListener) {
        mOpenChangeListener = openChangeListener;
    }

    public void setElevationHeight(int elevationHeight) {
        this.elevationHeight = elevationHeight;
        requestLayout();
    }

    public interface openChangeListener {

        void onScrolling(float percent);
    }


}
