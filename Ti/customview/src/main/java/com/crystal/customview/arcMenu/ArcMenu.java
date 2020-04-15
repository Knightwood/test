package com.crystal.customview.arcMenu;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;

import com.crystal.customview.R;

/**
 * 创建者 kiylx
 * 创建时间 2020/4/12 14:59
 */
public class ArcMenu extends ViewGroup implements View.OnClickListener {
    private static final String TAG = "arc菜单";
    //菜单按钮的默认位置
    private Position mPosition = Position.LEFT;
    //默认状态
    private Status mCurrentStatus = Status.CLOSE;
    //展开半径
    private int mRadius = 150;
    //菜单按钮
    private View mButton;
    private ClickInterface mClickInterface;

    @Override
    public void onClick(View v) {
        if (mButton == null) {
            mButton = getChildAt(0);
        }
        if (mCurrentStatus == Status.OPEN) {

            closeAnim();

        } else {
            mCurrentStatus = Status.OPEN;
            requestLayout();
        }


    }

    /**
     * 状态的枚举类
     */
    public enum Status {
        OPEN, CLOSE
    }

    /**
     * 设置菜单按钮在viewGroup中的位置，
     * 当ArcMenu在屏幕左侧的时候，那么，按钮就该在viewGroup的左边中间位置，反之，在右边中间位置
     */
    public enum Position {
        LEFT, RIGHT
    }

    /**
     * 调整菜单按钮在viewgroup中的位置，
     * 这样，在arcmenu放在左侧或是右侧的时候，避免arcmenu展开方向出错。
     * @param position LEFT或RIGHT
     */
    public void setPosition(Position position){
        this.mPosition=position;
    }

    /**
     * 返回arcmenu（也就是这个viewGroup）在父view中的尺寸
     * @return 返回一个Rect对象
     */
    public Rect getSize(){
        return new Rect(getLeft(),getTop(),getRight(),getBottom());
    }

    //构造函数
    public ArcMenu(Context context) {
        this(context, null);
    }

    public ArcMenu(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ArcMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        mRadius = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mRadius, getResources().getDisplayMetrics());
        //setBackgroundColor(getResources().getColor(R.color.huise));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        //MarginLayoutParams cParams;//视图的边距
        measureChildren(widthMeasureSpec, heightMeasureSpec);

        int mWidth = 0;//viewGroup最终的宽度
        int mHeight = 0;//viewGroup最终的高度
        View child;

        int count = getChildCount();

       /* for (int i = 0; i < count; i++) {
            child = getChildAt(i);
            //cParams = (MarginLayoutParams) child.getLayoutParams();
            mWidth += child.getMeasuredWidth();
            mHeight += child.getMeasuredHeight();
        }*/

        if (mCurrentStatus == Status.CLOSE) {
            child = getChildAt(0);
            mWidth = child.getMeasuredWidth();
            mHeight = child.getMeasuredHeight();

        } else {
            int verticalMaxView = 0;
            int horizonMaxView = 0;

            child = getChildAt(0);
            horizonMaxView = child.getWidth();
            verticalMaxView = child.getHeight();

            //viewGroup的尺寸，宽度等于最大的那个控件的两倍加上半径长，高度等于最大的那个控件的3倍加上2倍半径长
            mWidth = 2 * horizonMaxView + mRadius;
            mHeight = verticalMaxView + 2 * mRadius;
        }

        if (widthMode == MeasureSpec.AT_MOST && heightMode == MeasureSpec.AT_MOST) {

        } else {
            mWidth = resolveSizeAndState(mWidth, widthMeasureSpec, 0);
            mHeight = resolveSizeAndState(mHeight, heightMeasureSpec, 0);
        }

        setMeasuredDimension(mWidth, mHeight);

        //measureChildren(widthMeasureSpec, heightMeasureSpec);
        //setMeasuredDimension(getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec), getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec));
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int cl = 0;
        int ct = 0;
        int cr = getMeasuredWidth();
        int cb = getMeasuredHeight();

        View child;

        if (mButton == null)
            mButton = getChildAt(0);//点击按钮

        if (mCurrentStatus == Status.CLOSE) {
            //把菜单按钮放好位置
            mButton.layout(cl, ct, cr, cb);
            mButton.setOnClickListener(this);

        } else {
            //放置菜单按钮

            //菜单按钮的顶部和底部位置
            ct = getMeasuredHeight() / 2 - mButton.getMeasuredHeight() / 2;
            cb = ct + mButton.getMeasuredHeight();

            if (mPosition == Position.RIGHT) {
                cr = getMeasuredWidth();
                cl = cr - mButton.getMeasuredWidth();

            } else {//放在viewgroup的左边
                cl = 0;
                cr = cl + mButton.getMeasuredWidth();
            }
            mButton.layout(cl, ct, cr, cb);
            mButton.setOnClickListener(this);

            //展开菜单，把子view放在菜单按钮相同的位置
            int count = getChildCount();
            for (int i = 1; i < count; i++) {
                child = getChildAt(i);
                child.setVisibility(INVISIBLE);
                child.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mClickInterface!=null)
                        mClickInterface.click(v);
                        Log.d(TAG, "onClick:被点击的view的id： "+v.getId());
                        //scaleBigAnim(v);
                        closeAnim();
                    }
                });
                child.layout(cl, ct, cr, cb);
            }
            openAnim();//展开菜单的动画
        }


    }


    /**
     * 各个子view已经放置在了菜单按钮的位置，
     * 所以使用动画移动的时候，起始位置是0，结束位置是计算出来的
     */
    private void openAnim() {
        View child;
        float fx;
        float fy;

        spinButton();
        int count = getChildCount();
        for (int i = 1; i < count; i++) {
            child = getChildAt(i);
            child.setVisibility(VISIBLE);
            //视图平移后的最终位置
            fx = (float) (mRadius * Math.sin((Math.PI / count) * i));
            fy = (float) (mRadius * Math.cos((Math.PI / count) * i));

            ObjectAnimator translationX = new ObjectAnimator().ofFloat(child, "translationX", (float) 0, fx);
            ObjectAnimator translationY = new ObjectAnimator().ofFloat(child, "translationY", (float) 0, fy);

            ObjectAnimator spin = new ObjectAnimator().ofFloat(child, "rotation", 0f, 360f);

            ObjectAnimator alpha=new ObjectAnimator().ofFloat(child,"alpha",0f,1.0f);

            AnimatorSet animSet = new AnimatorSet();
            animSet.playTogether(translationX, translationY, spin,alpha);
            animSet.setDuration(500);
            animSet.start();

        }

    }

    /**
     * 使得菜单按钮旋转
     */
    private void spinButton() {
        ObjectAnimator spin = new ObjectAnimator().ofFloat(mButton, "rotation", 0f, 360f);
        spin.setDuration(500);
        spin.start();
    }

    private void closeAnim() {
        View child;

        int count = getChildCount();
        for (int i = 1; i < count; i++) {
            child = getChildAt(i);

            ObjectAnimator translationX = new ObjectAnimator().ofFloat(child, "translationX", child.getTranslationX(),0);
            ObjectAnimator translationY = new ObjectAnimator().ofFloat(child, "translationY", child.getTranslationY(), 0);

            ObjectAnimator spin = new ObjectAnimator().ofFloat(child, "rotation", 0f, 360f);

            ObjectAnimator alpha=new ObjectAnimator().ofFloat(child,"alpha",1.0f,0f);

            AnimatorSet animSet = new AnimatorSet();
            animSet.playTogether(translationX, translationY,spin,alpha);
            animSet.setDuration(500);
            animSet.start();
            animSet.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    mCurrentStatus = Status.CLOSE;
                    requestLayout();
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
        }

    }

    /**
     * 点击某一子view后的效果
     */
    private void scaleBigAnim(View v){
        ObjectAnimator scaleX=new ObjectAnimator().ofFloat(v,"scaleX",0f,1f);
        ObjectAnimator scaleY=new ObjectAnimator().ofFloat(v,"scaleY",0f,3f);
        ObjectAnimator alpha=new ObjectAnimator().ofFloat(v,"alpha",1.0f,0f);
        AnimatorSet set =new AnimatorSet();
        set.setDuration(300);
        set.setInterpolator(new DecelerateInterpolator());
        set.playTogether(scaleX,scaleY,alpha);
        set.start();
    }

    /**
     * 提供外界控制的接口
     */
    public interface ClickInterface{
        void click(View v);
    }
    public void setInterface(ClickInterface clickInterface){
        this.mClickInterface=clickInterface;
    }


}
