package com.crystal.customview.liteviewpager;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Matrix;
import android.text.Layout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

/**
 * 创建者 kiylx
 * 创建时间 2020/5/22 9:41
 */
public class LiteViewPager extends ViewGroup {

    private float mMinAlpha = 0.4F;
    private float mMinScale = 0.9F;
    private float mLastX, mLastY;//上一次的触摸坐标
    private int mTouchSlop;//触发滑动的最小距离
    private boolean isBeingDragged;//标记有没有在拖拽
    private float mOffsetX, mOffsetY;//水平和垂直偏移量
    private float mOffsetPercent;//偏移的百分比
    private boolean isReordered = false;//是否交换过层级顺序
    private boolean isFirstLayout = true;//第一次layout时指定基准线用于正确布局
    private ValueAnimator mAnimator;
    private long mFlingDuration=200L;//指定动画时长


    public LiteViewPager(Context context) {
        this(context, null);
    }

    public LiteViewPager(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LiteViewPager(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    /**
     * @param view   目标view
     * @param points 坐标点(x,y)
     * @return 坐标点是否在view的范围内
     * <p>
     * mapPointes():当矩阵发生变化后(旋转，缩放等)，将最初位置上面的坐标，转换成变化后的坐标,
     * 数组[0, 0]（分别对应x和y）在矩阵向右边平移了50(matrix.postTranslate(50, 0))之后，调用mapPoints方法并将这个数组作为参数传进去，那这个数组就变成[50, 0]，如果这个矩阵绕[100, 100]上的点顺时针旋转了90度(matrix.postRotate(90, 100, 100))的话，那这个数组就会变成[200, 0]了
     * <p>
     * 这里,先得到逆矩阵，也就是把做了变换的view的变换还原回去，比如向右平移50，invert后得到-50
     * 之后使用mapPoints，把当前的点击的相对于view内的坐标点映射到view没有做变换状态下的坐标值。这时就可以判断点击是不是在view内部
     */
    private boolean pointInView(View view, float[] points) {

        //点击时的坐标点减去这个view在父view中的位置，得到相对于这个view内的坐标。
        points[0] -= view.getLeft();
        points[1] -= view.getTop();

        //获取view的矩阵
        Matrix matrix = view.getMatrix();
        //如果view应用过矩阵变换
        //isIdentity():如果矩阵是恒等，则返回true
        if (!matrix.isIdentity()) {
            //获取逆矩阵
            matrix.invert(matrix);
            //映射坐标点
            matrix.mapPoints(points);
        }
        //判断坐标点是否在view内部
        return points[0] >= 0 && points[1] >= 0 && points[0] < view.getWidth() && points[1] < view.getHeight();

    }

    /*
     * 使用detachViewFromParent和attachViewToParent，移除view，再添加view就可以做到交换view的层级*/
//把view从parent中移除
    @Override
    public void detachViewFromParent(View child) {
        super.detachViewFromParent(child);
    }

    @Override
    public void detachViewFromParent(int index) {
        super.detachViewFromParent(index);
    }
    //把view添加进parent中

    @Override
    public void attachViewToParent(View child, int index, ViewGroup.LayoutParams params) {
        super.attachViewToParent(child, index, params);
    }

    /**
     * 把宽度定为子view的宽度之和，高度取最高的view的高度
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        //测量子view
        measureChildren(widthMeasureSpec, heightMeasureSpec);

        int width = measureWidth(widthMeasureSpec);
        int height = measureHeight(heightMeasureSpec);

        setMeasuredDimension(width, height);

    }

    /**
     * @param widthMeasureSpec 父布局的测量空间
     * @return 返回自己的宽度
     */
    private int measureWidth(int widthMeasureSpec) {
        int width = 0;

        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);

        LayoutParams layoutParams;//带有margin的lp

        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else {
            //如果宽度设置了wrap_content，则取全部子View的宽度和
            for (int i = 0; i < getChildCount(); i++) {
                View child = getChildAt(0);
                layoutParams = (LayoutParams) child.getLayoutParams();
                //这里的宽度加上了margin
                width += (child.getMeasuredWidth() + layoutParams.leftMargin + layoutParams.rightMargin);
            }
        }

        return width;
    }

    /**
     * @param heightMeasureSpec 父布局的测量空间
     * @return 返回自己的高度
     */
    private int measureHeight(int heightMeasureSpec) {
        int height = 0;
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        LayoutParams layoutParams;//带有margin的lp

        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else {
            int MaxHeight = 0;
            int tmp = 0;
            for (int i = 0; i < getChildCount(); i++) {
                View child = getChildAt(i);
                layoutParams = (LayoutParams) child.getLayoutParams();
                //加上了margin值的高度
                tmp = child.getMeasuredHeight() + layoutParams.topMargin + layoutParams.bottomMargin;

                if (tmp > MaxHeight)
                    MaxHeight = tmp;
            }

            height = MaxHeight;
        }


        return height;
    }

    /**
     * 比如，viewGroup中有3个子view，那么，把宽度分成4份，中间的view占据2份，两边的view分别占一份
     *
     * @param changed
     * @param l
     * @param t
     * @param r
     * @param b
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (isFirstLayout) {
                layoutChild(child, getFirstLayoutBaseline(child));
            } else {
                layoutChild(child, getBaseLineByChild(child));
            }
        }
        isFirstLayout = false;

    }

    /**
     * @param child viewGroup中的子view
     * @return 返回那条分割线(在viewGroup中的x值 ， 也就是子view 在layout时的left值)，分割线用于确定view位置
     */
    private int getBaseLineByChild(View child) {
        //位置：最左边：0；中间：2；最右边：1

        //根据子View在ViewGroup中的索引计算基准线
        int baseLineLeft = getWidth() / 4;//左边的线 (最底的子View放在左边)
        int baseLineCenter = getWidth() / 2;//中间的线 (最顶的子View放在中间)
        int baseLineRight = getWidth() - baseLineLeft;//右边的线 (接着放在右边)
        int baseLine = 0;
        LayoutParams lp = (LayoutParams) child.getLayoutParams();

        switch (lp.from) {
            case 0:
                switch (lp.to) {
                    case 1:
                        //目的地是1，证明手指正在向左滑动，所以下面的mOffsetPercent是用负的
                        //当前基准线 = 初始基准线 + 与目标基准线(现在是右边)的距离 * 滑动百分比
                        //当滑动百分比是1时，这里的baseline就等于右边的那个baseline的值
                        baseLine = (int) (baseLineLeft + ((baseLineRight - baseLineLeft) * -mOffsetPercent));
                        break;
                    case 2:
                        //如果目的地是中间(2)，那目标基准线就是ViewGroup宽度的一半了(baseLineCenter)，计算方法同上
                        baseLine = (int) (baseLineLeft + ((baseLineCenter - baseLineLeft) * mOffsetPercent));
                        break;
                    default:
                        baseLine = baseLineLeft;
                        break;
                }
                break;
            case 1:
                //原理同上
                switch (lp.to) {
                    case 0:
                        baseLine = (int) (baseLineRight + ((baseLineRight - baseLineLeft) * -mOffsetPercent));
                        break;
                    case 2:
                        baseLine = (int) (baseLineRight + ((baseLineRight - baseLineCenter) * mOffsetPercent));
                        break;
                    default:
                        baseLine = baseLineRight;
                        break;
                }
                break;
            case 2:
                switch (lp.to) {
                    case 0:
                        baseLine = (int) (baseLineCenter + ((baseLineCenter - baseLineLeft) * mOffsetPercent));
                        break;
                    case 1:
                        baseLine = (int) (baseLineCenter + ((baseLineRight - baseLineCenter) * mOffsetPercent));
                        break;
                    default:
                        baseLine = baseLineCenter;
                        break;
                }
                break;
        }
        return baseLine;


    }

    private int getFirstLayoutBaseline(View child) {
        //根据子View在ViewGroup中的索引计算基准线
        switch (indexOfChild(child)) {
            case 0:
                return getWidth() / 4; //左边的线 (最底的子View放在左边)
            case 1:
                return getWidth() / 2 + getWidth() / 4; //右边的线 (接着放在右边)
            case 2:
                return getWidth() / 2; //中间的线 (最顶的子View放在中间)
            default:
                return 0;
        }
    }

    /**
     * @param child    viewGroup中的子view
     * @param baseLine 分割线
     *                 依据分割线摆放child
     */
    private void layoutChild(View child, int baseLine) {
        //获取child的测量宽高
        int childWidth = child.getMeasuredWidth();
        int childHeight = child.getMeasuredHeight();
        //viewGroup的高度的一半
        int CenterY = getHeight() / 2;
        //根据baseLine确定left和right
        int left = baseLine - childWidth / 2;
        int right = left + childWidth;

        int top = CenterY - childHeight / 2;
        int bottom = CenterY + top;

        LayoutParams lp = (LayoutParams) child.getLayoutParams();

        if (isFirstLayout) {
            //初始化from和to信息，第一次layout之后，isFirstLayout将变为false
            lp.from = lp.to = indexOfChild(child);
        }

        //更新不透明度和缩放比例
        child.setAlpha(lp.alpha);
        child.setScaleX(lp.scale);
        child.setScaleY(lp.scale);

        child.layout(left + lp.leftMargin + getPaddingLeft(),
                top + lp.topMargin + getPaddingTop(),
                right + lp.leftMargin - getPaddingRight(),
                bottom + lp.topMargin - getPaddingBottom());

    }

    /**
     * 把LayoutParams中的scale和alpha在addView方法里初始化
     * 顺便限制一下子View的个数
     *
     * @param child
     * @param index
     * @param params
     */
    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        //超过一定数量的子view就抛出错误
        int childCount = getChildCount();
        if (childCount > 2) {
            throw new IllegalStateException("最多只能有3个子view");
        }
        //如果传进来的LayoutParams不是我们自定义的LayoutParams的话，就要创建一个
        LayoutParams lp = params instanceof LayoutParams ? (LayoutParams) params : new LayoutParams(params);
        if (childCount < 2) {
            lp.alpha = mMinAlpha;
            lp.scale = mMinScale;
        } else {
            lp.alpha = 1F;
            lp.scale = 1F;
        }

        super.addView(child, index, params);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        float x = ev.getX();
        float y = ev.getY();
        /*if (ev.getAction() == MotionEvent.ACTION_MOVE && isBeingDragged||super.onInterceptTouchEvent(ev)) {
            return true;
        }*/
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //更新上一次的触摸坐标
                mLastX = x;
                mLastY = y;
                //开始滑动前先打断动画
                abortAnimation();
                break;
            case MotionEvent.ACTION_MOVE:
                //移动的距离
                float offsetX = x - mLastY;
                float offsetY = y - mLastY;
                //判断是否触发拖动事件
                if (Math.abs(offsetX) > mTouchSlop || Math.abs(offsetY) > mTouchSlop) {
                    //更新上一次的触摸坐标,此时mLastY和mLastX是刚刚开始可以滑动时的坐标值
                    mLastY = y;
                    mLastX = x;
                    //标记已开始拖拽
                    isBeingDragged = true;
                }
                break;

            case MotionEvent.ACTION_UP:
                //标记没有在拖拽
                isBeingDragged = false;
                handleActionUp(x, y);
                break;
        }

        return isBeingDragged;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //x,y，拦截触摸事件后当滑动距离超过最小滑动距离时，在此处获得此时的坐标
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //开始滑动前先打断动画
                abortAnimation();
            case MotionEvent.ACTION_MOVE:
                float offsetX = x - mLastX;//超出最小滑动距离后的偏移量
                mOffsetX += offsetX;//总的偏移量
                onItemMove();
                break;
            case MotionEvent.ACTION_UP:
                isBeingDragged = false;
                handleActionUp(x, y);//松开手指时播放动画
                break;
        }
        mLastX = x;
        mLastY = y;

        Log.d(TAG, "onTouchEvent: " + "mLastX：" + mLastX + "mLastY: " + mLastY + "点击： " + x + "---" + y);

        return true;
    }

    private void handleActionUp(float x, float y) {
        playFixingAnimation();
    }

    private void playFixingAnimation() {
        //没有子view就直接返回
        if (getChildCount() == 0) {
            return;
        }
        //起始点，就是当前的滑动距离
        float start = mOffsetX;
        float end;
        //如果滑动距离超过一半，顺势移动下去也就是end是viewGroup的宽度,否则，视图回退，也就是end是0
        if (mOffsetPercent >= 0.5) { //如果滑动百分比是正数，表示是向右滑了>50%，所以目的地就是宽度
            end = getWidth();
        } else if (mOffsetPercent <= -0.5) {//相反，如果是负数，那就拿负的宽度
            end = -getWidth();
        } else
            end = 0; //如果滑动没超过50%，那就把距离变成0，也就是回退了
        startValueAnimator(start, end);

    }

    private void startValueAnimator(float start, float end) {
        if (start == end) {
            //起始点和结束点一样，退出
            return;
        }
        //先打断之前的动画，如果正在播放的话
        abortAnimation();

        mAnimator = ValueAnimator.ofFloat(start, end)
                .setDuration(mFlingDuration);
        mAnimator.setInterpolator(mInterpolator);
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Float currentValue = (float) animation.getAnimatedValue();
                mOffsetX = currentValue;//开始结束是在0到viewgroup的宽度范围内，mOffsetX也是这个范围
                //处理子View的移动行为
                onItemMove();
            }
        });
        //mAnimator.addListener(mAnimatorListener);
        //ValueAnimatorUtil.resetDurationScale();
        mAnimator.start();
    }

    /**
     * 调整动画的插值器，现在是减速
     */
    private Interpolator mInterpolator = new DecelerateInterpolator();

    /**
     * 取消动画播放
     */
    private void abortAnimation() {
        if (mAnimator != null && mAnimator.isRunning()) {
            mAnimator.cancel();
        }
    }

    private void onItemMove() {
        //更新滑动百分比,总的滑动偏移量除以viewGroup的宽度得到相对于viewGroup宽度的偏移百分比
        mOffsetPercent = mOffsetX / getWidth();
        //更新子View的出发点和目的地
        updateChildrenFromAndTo();
        //更新子View的层级顺序
        updateChildrenOrder();
        //更新子View的不透明度和缩放比例
        updateChildrenAlphaAndScale();
        //请求重新布局
        requestLayout();
        Log.d(TAG, "onItemMove: 滑动百分比: " + mOffsetPercent + "移动距离: " + mOffsetX + "整体的宽度: " + getWidth() + "是否交换过顺序： " + isReordered);
    }

    private static final String TAG = "litePager";

    /**
     * 更新出发点和目的地(updateChildrenFromAndTo())
     */
    private void updateChildrenFromAndTo() {
        //如果滑动的距离>=ViewGroup宽度
        if (Math.abs(mOffsetPercent) >= 1) {
            //遍历子View，标记已经到达目的地
            for (int i = 0; i < getChildCount(); i++) {
                LayoutParams lp = (LayoutParams) getChildAt(i).getLayoutParams();
                lp.from = lp.to;
            }
            //重置层级交换标记
            isReordered = false;
            //处理溢出: 比如总宽度是100，现在是120，那么处理之后会变成20
            mOffsetX %= (float) getWidth();
            mOffsetPercent %= 1;
        } else {
            //遍历子View，并根据当前滑动的百分比来更新子View的目的地
            for (int i = 0; i < getChildCount(); i++) {
                LayoutParams lp = (LayoutParams) getChildAt(i).getLayoutParams();

                switch (lp.from) {
                    //最左边：0；中间：2；最右边：1
                    //最左边的子View，如果是向右滑动的话，那么它的目的地是中间，也就是2了
                    //如果是向左滑动的话，目的地是最右边的位置，也是1了，下面同理
                    case 0:
                        lp.to = mOffsetPercent > 0 ? 2 : 1;
                        break;
                    case 1:
                        lp.to = mOffsetPercent > 0 ? 0 : 2;
                        break;
                    case 2:
                        lp.to = mOffsetPercent > 0 ? 1 : 0;
                        break;
                    default:
                        break;
                }

            }

        }
    }

    /**
     * 每次滑动距离超过50%的时候只会交换一次顺序，
     * 除了这个还要处理回退的问题，也就是滑动超过一半后(这时已经交换过顺序)，
     * 又反方向滑动，这时候也要交换一次顺序
     * <p>
     * 三个view的情况下，中间位置的view处于最顶层，
     * 当向左滑动时最右侧的view应该滑动到中间位置，且应该变成最顶层，
     * 而原中间位置的view向左移动，层级降一级
     */
    private void updateChildrenOrder() {
        //位置：最左边：0；中间：2；最右边：1
        //如果滑动距离超过了ViewGroup宽度的一半，且还没有交换层级顺序
        //就把索引为1，2的子View交换顺序，并标记已经交换过
        if (Math.abs(mOffsetPercent) > 0.5) {
            if (!isReordered) {
                exchangeOrder(1, 2);
                isReordered = true;
            }
        } else {
            //滑动距离没有超过宽度一半，即有可能是滑动超过一半然后滑动回来
            //如果isReordered=true，就表示本次滑动已经交换过顺序
            //所以要再次交换一下
            if (isReordered) {
                exchangeOrder(1, 2);
                isReordered = false;
            }
        }

    }

    private void exchangeOrder(int fromIndex, int toIndex) {
        if (fromIndex == toIndex) {
            return;
        }
        View from = getChildAt(fromIndex);
        View to = getChildAt(toIndex);
//分离出来
        detachViewFromParent(from);
        detachViewFromParent(to);

        //重新放回去，但是index互换了
        attachViewToParent(from, (toIndex > getChildCount()) ? getChildCount() :
                toIndex, from.getLayoutParams());
        attachViewToParent(to, (fromIndex > getChildCount()) ? getChildCount() :
                fromIndex, to.getLayoutParams());

        //重绘视图
        invalidate();
    }

    /**
     * 遍历子view，改变缩放和透明度
     */
    private void updateChildrenAlphaAndScale() {
        for (int i = 0; i < getChildCount(); i++) {
            updateAlphaAndScale(getChildAt(i));
        }
    }

    private void updateAlphaAndScale(View childAt) {
        //位置：最左边：0；中间：2；最右边：1
        LayoutParams lp = (LayoutParams) childAt.getLayoutParams();

        switch (lp.from) {
            case 0:
                switch (lp.to) {
                    case 1: //如果它目的地是最右边的话
                        //要把它放在最底，为了避免在移动过程中遮挡其他子View
                        setAsBottom(childAt);
                        //透明度和缩放比例都不用变，因为现在就已经满足条件了
                        break;
                    case 2://如果它要移动到中间
                        //根据滑动比例来计算出当前的透明度和缩放比例
                        lp.alpha = mMinAlpha + (1F - mMinAlpha) * mOffsetPercent;
                        lp.scale = mMinScale + (1F - mMinScale) * mOffsetPercent;
                        break;
                }
                break;
            case 1:
                switch (lp.to) {
                    case 0:
                        //把它放在最底，避免在移动过程中遮挡其他子View
                        setAsBottom(childAt);
                        //透明度和缩放比例都不用变
                        break;
                    case 2:
                        //这里跟上面唯一不同的地方就是mOffsetPercent要取负的
                        //因为它向中间移动的时候，mOffsetPercent是负数，这样做就刚好抵消
                        lp.alpha = mMinAlpha + (1F - mMinAlpha) * -mOffsetPercent;
                        lp.scale = mMinScale + (1F - mMinScale) * -mOffsetPercent;
                        break;
                }
                break;
            case 2:
                //这里不用判断to了，因为无论向哪一边滑动，不透明度和缩放比例都是减少
                lp.alpha = 1F - (1F - mMinAlpha) * Math.abs(mOffsetPercent);
                lp.scale = 1F - (1F - mMinScale) * Math.abs(mOffsetPercent);
                break;
        }
    }

    /**
     * 把目标子View的索引跟索引0交换顺序：
     *
     * @param child
     */
    private void setAsBottom(View child) {
        //获取child索引后跟0交换层级顺序,层级越大，显示位置越是在其他view上层
        exchangeOrder(indexOfChild(child), 0);
    }

    /**
     * 两边的子View会变小和变透明，那么这些属性(缩放比例、透明度)肯定要用变量保存起来的，
     * 我们可以扩展LayoutParams，把这些属性都放在LayoutParams里面：
     * 因为我们的ViewGroup需要支持Margin，所以继承自MarginLayoutParams：
     */
    static class LayoutParams extends MarginLayoutParams {
        int to, from;
        float scale;
        float alpha;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }

    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    @Override
    protected LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }
}


/**
 * 把被点击的view放在最顶层
 *
 * @param beClickedView 被点击的view。
 */
    /*public void moveToTop(View beClickedView) {

    }*/