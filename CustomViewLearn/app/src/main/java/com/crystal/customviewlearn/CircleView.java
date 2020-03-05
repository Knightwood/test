package com.crystal.customviewlearn;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * 创建者 kiylx
 * 创建时间 2020/3/4 19:59
 */
public class CircleView extends View {

    Paint mPaint1;

    //在java代码里new的，会调用这一个
    public CircleView(Context context) {
        super(context);

        //在构造函数里初始化画笔操作
        init();
    }

    //view在xml中定义的，调用这一个,自定义属性是从AttributeSet参数传进来的
    public CircleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        init();
    }
    // 不会自动调用
// 一般是在第二个构造函数里主动调用
// 如View有style属性时
    public CircleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }



    //API21之后才使用
    // 不会自动调用
    // 一般是在第二个构造函数里主动调用
    // 如View有style属性时
    public CircleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    //画笔初始化
    private void init() {
        //创建画笔
        mPaint1=new Paint();
        //设置画笔颜色为蓝色
        mPaint1.setColor(Color.BLUE);
        //设置画笔宽度
        mPaint1.setStrokeWidth(5f);
        //画笔模式为填充
        mPaint1.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int withMode = MeasureSpec.getMode(widthMeasureSpec);
        int withSize=MeasureSpec.getSize(widthMeasureSpec);

        int heightMode=MeasureSpec.getMode(heightMeasureSpec);
        int heightSize=MeasureSpec.getSize(heightMeasureSpec);

        // 设置wrap_content的默认宽 / 高值
        // 默认宽/高的设定并无固定依据,根据需要灵活设置
        // 类似TextView,ImageView等针对wrap_content均在onMeasure()对设置默认宽 / 高值有特殊处理
        int mWidth =300;
        int mHeight=300;

        // 当布局参数设置为wrap_content时，设置默认值
        /*if (getLayoutParams().width== ViewGroup.LayoutParams.WRAP_CONTENT&&getLayoutParams().height==ViewGroup.LayoutParams.WRAP_CONTENT){
            setMeasuredDimension(mWidth,mHeight);
        } else if (getLayoutParams().width == ViewGroup.LayoutParams.WRAP_CONTENT) {
            //宽度是适应内容
            setMeasuredDimension(mWidth, heightSize);
        }else if(getLayoutParams().height==ViewGroup.LayoutParams.WRAP_CONTENT){
            setMeasuredDimension(withSize,mHeight);
        }*/

        /*上面处理wrap_content：
        其实可以参考一下ImageView当wrap_content是如何处理的；
        在View类中有这样一个方法：int resolveSizeAndState(int size, int measureSpec, int childMeasuredState);
        设置一个固定值应对AT MOST，再调用resolveSizeAndState，再setMeasuredDimension()就完事了。*/
        mWidth=resolveSizeAndState(mWidth,widthMeasureSpec,0);
        mHeight=resolveSizeAndState(mHeight,heightMeasureSpec,0);
        setMeasuredDimension(mWidth,mHeight);

    }

    //重载onDraw()进行绘制
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //获取控件的宽度和高度
        int width =getWidth();
        int height =getHeight();

        //设置圆的半径=(宽,高)最小值的1/2
        int r=Math.min(width,height)/2;
        //画出圆形
        //圆心是控件的中央，半径上面写好了
        canvas.drawCircle(width/2,height/2,r,mPaint1);
    }
}
