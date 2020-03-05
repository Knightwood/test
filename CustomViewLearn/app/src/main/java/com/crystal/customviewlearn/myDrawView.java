package com.crystal.customviewlearn;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * 创建者 kiylx
 * 创建时间 2020/3/5 17:44
 */
public class myDrawView extends View {

    private Paint mPrint;//画笔，绘制线条的Path
    private Path mPath;//记录用户绘制的Path
    private Canvas mCanvas;//内存中创建的Canvas
    private Bitmap mBitmap;//缓存绘制的内容

    private int mLastX;
    private int mLastY;

    public myDrawView(Context context) {
        super(context);
        init();
    }

    public myDrawView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public myDrawView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public myDrawView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        mPath = new Path();
        mPrint = new Paint();//初始化画笔
        mPrint.setColor(Color.BLUE);
        mPrint.setAntiAlias(true);//是否启用抗锯齿
        mPrint.setStrokeWidth(20);//设置线宽
        mPrint.setStrokeCap(Paint.Cap.ROUND);//设置转弯处为圆角
        mPrint.setStrokeJoin(Paint.Join.ROUND);//结合处为圆角
        mPrint.setStyle(Paint.Style.STROKE);//画出来的是线不是填充
        mPrint.setDither(true);//防抖动
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
/*View的高宽是由View本身和Parent容器共同决定的。
getMeasuredWidth()和getWidth()分别对应于视图绘制的measure和layout阶段。
getMeasuredWidth()获取的是View原始的大小，也就是这个View在XML文件中配置或者是代码中设置的大小。
getWidth()获取的是这个View最终显示的大小，
这个大小有可能等于原始的大小，也有可能不相等。
比如说，在父布局的onLayout()方法或者该View的onDraw()方法里调用measure(0, 0)，
二者的结果可能会不同（measure中的参数可以自己定义）。
*/
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();

        //初始化bitmap，canvas
        mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawPath();
        canvas.drawBitmap(mBitmap, 0, 0, null);
    }

    //绘制线条
    private void drawPath() {
        mCanvas.drawPath(mPath, mPrint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        int x = (int) event.getX();
        int y = (int) event.getY();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mLastX=x;
                mLastY=y;
                mPath.moveTo(mLastX,mLastY);
                break;
            case MotionEvent.ACTION_MOVE:
                int dx=Math.abs(x-mLastX);
                int dy=Math.abs(y-mLastX);
                if (dx>3||dy>3){
                    mPath.lineTo(x,y);
                    mLastX=x;
                    mLastY=y;
                    break;
                }
        }
        //invalidate:无效;
        invalidate();//刷新View,如果视图是可见的，不久就会调用onDraw()方法，这个方法必须在ui线程调用，在非ui线程则使用postInvalidate()
        return true;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }


}
