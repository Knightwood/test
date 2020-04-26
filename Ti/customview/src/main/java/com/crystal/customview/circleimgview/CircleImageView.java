package com.crystal.customview.circleimgview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;

import androidx.appcompat.widget.AppCompatImageView;

import androidx.annotation.Nullable;

/**
 * 创建者 kiylx
 * 创建时间 2020/4/18 16:33
 */
public class CircleImageView extends AppCompatImageView {
int radius;

    public CircleImageView(Context context) {
        super(context);
    }

    public CircleImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CircleImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int length=Math.min(getMeasuredWidth(),getMeasuredHeight());
        radius=length;

        //setMeasuredDimension(length,length);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Drawable drawable=getDrawable();
        if (drawable==null){
            return;
        }
        Bitmap bitmap=((BitmapDrawable) drawable).getBitmap();

        BitmapShader shader=new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);

        Paint paint=new Paint();
        paint.setShader(shader);

        canvas.drawCircle(radius/2,radius/2,radius,paint);

    }
    public int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dp, getResources().getDisplayMetrics());
    }

}
