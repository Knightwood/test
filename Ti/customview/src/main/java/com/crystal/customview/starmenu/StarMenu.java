package com.crystal.customview.starmenu;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.OvershootInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;

import com.crystal.customview.R;

/**
 * 创建者 kiylx
 * 创建时间 2020/4/10 22:04
 */
public class StarMenu extends ViewGroup implements View.OnClickListener {
    private static final String TAG = "星星菜单";

    /**
     * 菜单显示位置
     */
    private Position mPosition = Position.LEFT_TOP;
    /**
     * 菜单显示半径
     */
    private int mRadius = 100;
    /**
     * 用户点击的按钮
     */
    private View mButton;
    /**
     * 当前星星菜单的状态
     */
    private Status mCurrentStatus = Status.CLOSE;
    /**
     * 回调接口
     */
    private OnMenuItemClickListener menuItemClickListener;

    //构造函数
    public StarMenu(Context context) {
        this(context, null);
    }

    public StarMenu(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StarMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //dp转成px
        mRadius = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mRadius, getResources().getDisplayMetrics());
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.StarMenu, defStyleAttr, 0);
        switch (a.getInt(R.styleable.StarMenu_position, 0)) {
            case 0:
                mPosition = Position.LEFT_TOP;
                break;
            case 1:
                mPosition = Position.RIGHT_TOP;
                break;
            case 2:
                mPosition = Position.RIGHT_BOTTOM;
                break;
            case 3:
                mPosition = Position.LEFT_BOTTOM;
                break;

        }
        //获取半径
        mRadius = a.getDimensionPixelSize(R.styleable.StarMenu_radius, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100f, getResources().getDisplayMetrics()));

        a.recycle();

    }

    @Override
    public void onClick(View v) {
        if (mButton == null) {
            mButton = getChildAt(0);
            rotateView(mButton, 0f, 270f, 300);
            toggleMenu(300);
        }
    }


    /**
     * 按钮的旋转动画
     *
     * @param view
     * @param fromDegrees
     * @param toDegrees
     * @param durationMillis
     */
    public static void rotateView(View view, float fromDegrees,
                                  float toDegrees, int durationMillis) {
        RotateAnimation rotate = new RotateAnimation(fromDegrees, toDegrees,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        rotate.setDuration(durationMillis);
        rotate.setFillAfter(true);
        view.startAnimation(rotate);
    }

    public void toggleMenu(int durationMillis) {
        int count = getChildCount();
        for (int i = 0; i < count - 1; i++) {
            final View childView = getChildAt(i + 1);
            childView.setVisibility(View.VISIBLE);

            int xflag = 1;
            int yflag = 1;

            if (mPosition == Position.LEFT_TOP
                    || mPosition == Position.LEFT_BOTTOM)
                xflag = -1;
            if (mPosition == Position.LEFT_TOP
                    || mPosition == Position.RIGHT_TOP)
                yflag = -1;

            // child left
            int cl = (int) (mRadius * Math.sin(Math.PI / 2 / (count - 2) * i));
            // child top
            int ct = (int) (mRadius * Math.cos(Math.PI / 2 / (count - 2) * i));

            AnimationSet animset = new AnimationSet(true);
            Animation animation = null;
            if (mCurrentStatus == Status.CLOSE) {// to open
                animset.setInterpolator(new OvershootInterpolator(2F));
                animation = new TranslateAnimation(xflag * cl, 0, yflag * ct, 0);
                childView.setClickable(true);
                childView.setFocusable(true);
            } else {// to close
                animation = new TranslateAnimation(0f, xflag * cl, 0f, yflag
                        * ct);
                childView.setClickable(false);
                childView.setFocusable(false);
            }
            animation.setAnimationListener(new Animation.AnimationListener() {
                public void onAnimationStart(Animation animation) {
                }

                public void onAnimationRepeat(Animation animation) {
                }

                public void onAnimationEnd(Animation animation) {
                    if (mCurrentStatus == Status.CLOSE)
                        childView.setVisibility(View.GONE);

                }
            });

            animation.setFillAfter(true);
            animation.setDuration(durationMillis);
            // 为动画设置一个开始延迟时间，纯属好看，可以不设
            animation.setStartOffset((i * 100) / (count - 1));
            RotateAnimation rotate = new RotateAnimation(0, 720,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f);
            rotate.setDuration(durationMillis);
            rotate.setFillAfter(true);
            animset.addAnimation(rotate);
            animset.addAnimation(animation);
            childView.startAnimation(animset);
            final int index = i + 1;
            childView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (menuItemClickListener != null)
                        menuItemClickListener.onClick(childView, index - 1);
                    menuItemAnin(index - 1);
                    changeStatus();

                }
            });

        }
        changeStatus();
        Log.e(TAG, mCurrentStatus.name() + "");
    }

    //保存动画后的状态
    private void changeStatus() {
        mCurrentStatus = mCurrentStatus == Status.OPEN ? Status.CLOSE : Status.OPEN;
    }
    /**
     * 开始菜单动画，点击的MenuItem放大消失，其他的缩小消失
     * @param item
     */
    private void menuItemAnin(int item)
    {
        for (int i = 0; i < getChildCount() - 1; i++)
        {
            View childView = getChildAt(i + 1);
            if (i == item)
            {
                childView.startAnimation(scaleBigAnim(300));
            } else
            {
                childView.startAnimation(scaleSmallAnim(300));
            }
            childView.setClickable(false);
            childView.setFocusable(false);

        }

    }

    /**
     * 缩小消失
     * @param durationMillis
     * @return
     */
    private Animation scaleSmallAnim(int durationMillis)
    {
        Animation anim = new ScaleAnimation(1.0f, 0f, 1.0f, 0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        anim.setDuration(durationMillis);
        anim.setFillAfter(true);
        return anim;
    }
    /**
     * 放大，透明度降低
     * @param durationMillis
     * @return
     */
    private Animation scaleBigAnim(int durationMillis)
    {
        AnimationSet animationset = new AnimationSet(true);

        Animation anim = new ScaleAnimation(1.0f, 4.0f, 1.0f, 4.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        Animation alphaAnimation = new AlphaAnimation(1, 0);
        animationset.addAnimation(anim);
        animationset.addAnimation(alphaAnimation);
        animationset.setDuration(durationMillis);
        animationset.setFillAfter(true);
        return animationset;
    }

    /**
     * 状态的枚举类
     */
    public enum Status {
        OPEN, CLOSE
    }

    /**
     * 设置菜单现实的位置，四选1，默认右下
     */
    public enum Position {
        LEFT_TOP, RIGHT_TOP, RIGHT_BOTTOM, LEFT_BOTTOM;
    }

    public interface OnMenuItemClickListener {
        void onClick(View view, int pos);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec),
                getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec));

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (changed) {
            layoutButton();
            int count = getChildCount();
            for (int i = 0; i < count - 1; i++) {

                View child = getChildAt(i + 1);
                child.setVisibility(GONE);//先隐藏起来

                int cl = (int) (mRadius * Math.sin(Math.PI / 2 / (count - 2) * i));//count包含了展开菜单的按钮，所以减二
                int ct = (int) (mRadius * Math.cos(Math.PI / 2 / (count - 2) * i));

                //childrenview width
                int cWidth = child.getMeasuredWidth();
                int cHeight = child.getMeasuredHeight();

                //左下右下
                if (mPosition == Position.LEFT_BOTTOM || mPosition == Position.RIGHT_BOTTOM) {
                    ct = getMeasuredHeight() - cHeight - ct;
                }
                //右上右下
                if (mPosition == Position.RIGHT_TOP || mPosition == Position.RIGHT_BOTTOM) {
                    cl = getMeasuredWidth() - cWidth - cl;
                }

                child.layout(cl, ct, cl + cWidth, ct + cHeight);
            }
        }

    }

    private void layoutButton() {
        View cButton = getChildAt(0);
        cButton.setOnClickListener(this);
        int l = 0;
        int t = 0;
        int width = cButton.getMeasuredWidth();
        int height = cButton.getMeasuredHeight();
        switch (mPosition) {
            case LEFT_TOP:
                l = 0;
                t = 0;
                break;
            case LEFT_BOTTOM:
                l = 0;
                t = getMeasuredHeight() - height;
                break;
            case RIGHT_TOP:
                l = getMeasuredWidth() - width;
                t = 0;
                break;
            case RIGHT_BOTTOM:
                l = getMeasuredWidth() - width;
                t = getMeasuredHeight() - height;
                break;
        }
        cButton.layout(l, t, l + width, t + height);
    }
}
/*@Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int count = getChildCount();
        int childMeasureWidth = 0;
        int childMeasuredHeight = 0;
        int layoutWidth = 0;//容器占据的宽度
        int layoutHeight = 0;//容器占据的高度
        int maxChildHeight = 0;//一行子控件的最高高度，超过此高度，另起一行显示

        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);

            //onlayout执行完才能使用getWidth()和getHeight()
            childMeasureWidth = child.getMeasuredWidth();
            childMeasuredHeight = child.getMeasuredHeight();
            if (layoutWidth < getWidth()) {
                l = layoutWidth;
                r = l + childMeasureWidth;
                t = layoutHeight;
                b = t + childMeasuredHeight;
            } else {
                //此时已经达到了最大宽度，换行显示（left置0，height翻倍）
                l = 0;
                layoutHeight += maxChildHeight;
                maxChildHeight = 0;

                l = layoutWidth;
                r = l + childMeasureWidth;
                t = layoutHeight;
                b = t + childMeasuredHeight;
            }

            layoutWidth += childMeasureWidth;//宽度占用空间累加
            if (childMeasuredHeight > maxChildHeight) {
                //使用上一行中最高的view的高度，以便折行显示时作为顶部起点
                maxChildHeight = childMeasuredHeight;
            }

            child.layout(l, t, r, b);

        }


    }*/