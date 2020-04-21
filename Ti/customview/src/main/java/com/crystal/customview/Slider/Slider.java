package com.crystal.customview.Slider;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.crystal.customview.R;

import static com.crystal.customview.Slider.Slider.shiftPos.LEFT;
import static com.crystal.customview.Slider.Slider.shiftPos.RIGHT;
import static com.crystal.customview.Slider.Slider.shiftPos.STOP;

/**
 * 创建者 kiylx
 * 创建时间 2020/4/18 11:50
 */
public class Slider extends ConstraintLayout {
    private static final String TAG = "滑竿";

    private ConstraintLayout mRoot;
    private ImageView sliderButton;
    private ImageView strip;
    private int x;//vbuttonView的x位置
    int[] stripLocation =new int[2];//滑动条在屏幕中的位置

    private changeStripSize changeInterface;//改变滑动条大小的接口
    private Play mPlay;//发送向左或向右的消息

    public Slider(Context context) {
        super(context);
    }

    public Slider(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.slider, this);
        initAttr(context, attrs);
    }

    public Slider(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.slider, this);
        initAttr(context, attrs);
    }

    private void initAttr(Context context, AttributeSet attrs) {
        mRoot = findViewById(R.id.slider_root);
        sliderButton = mRoot.findViewById(R.id.slider_button);
        strip = mRoot.findViewById(R.id.strip);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.Slider);

        int buttonWidth = a.getDimensionPixelSize(R.styleable.Slider_sliderButtonHeight, -1);
        int sliderWidth = a.getDimensionPixelSize(R.styleable.Slider_sliderWidth, -1);
        int sliderHeight = a.getDimensionPixelSize(R.styleable.Slider_sliderHeight, -1);

        if (buttonWidth > 0) {
            LayoutParams params = new LayoutParams(buttonWidth, buttonWidth);
            sliderButton.setLayoutParams(params);
        }
        if (sliderWidth > 0 && sliderHeight > 0) {
            LayoutParams params = new LayoutParams(sliderWidth, sliderHeight);
            strip.setLayoutParams(params);
        } else {
            if (sliderWidth > 0) {
                LayoutParams params = new LayoutParams(sliderWidth, ViewGroup.LayoutParams.MATCH_PARENT);
                strip.setLayoutParams(params);
            } else if (sliderHeight > 0) {
                LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, sliderHeight);
                strip.setLayoutParams(params);
            }
        }

        int buttonRes = a.getResourceId(R.styleable.Slider_sliderButtonBackground, R.drawable.slider_button);
        int stripRes = a.getResourceId(R.styleable.Slider_sliderBackground, R.drawable.strip);

        sliderButton.setBackgroundResource(buttonRes);
        strip.setBackgroundResource(stripRes);
        a.recycle();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        strip.getLocationOnScreen(stripLocation);
        x =getWidth()/2-sliderButton.getWidth() / 2;
        moveButton();
    }


    @SuppressLint("ClickableViewAccessibility")
    private void moveButton() {
        sliderButton.setOnTouchListener(new MoveView());
    }

    private class MoveView implements View.OnTouchListener {
        int lastX;
        int nowX;
        int dX;
        int direction=0;//滑动的方向，大于0发送向右移动的消息，反之发送向左的消息
        int l;

        @Override
        public boolean onTouch(View v, MotionEvent event) {

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (changeInterface != null) {
                        changeInterface.changeSize(strip);
                    }
                    lastX = (int) event.getRawX();
                    break;
                case MotionEvent.ACTION_MOVE:
                    nowX=(int)event.getRawX();
                    dX =nowX-lastX;
                    if (direction==0){
                        //初始时是0，移动时在这里赋值一次，保持在action_up前direction的值不变
                        direction=dX;
                    }

                    if (Math.abs(nowX-lastX) > ViewConfiguration.get(getContext()).getScaledTouchSlop()) {
                        Log.d(TAG, "onTouch:当前 "+nowX+"--左"+strip.getLeft()+"--右"+strip.getRight());
                        Log.d(TAG, "onTouch:当前绝对 "+nowX+"--dX"+ dX);
                        if (dX <0&&nowX<=stripLocation[0]) {
                            l=strip.getLeft();
                            Log.d(TAG, "onTouch: 超出左边空间");
                        } else if (dX >0&&nowX>=stripLocation[0]+strip.getWidth()) {
                            Log.d(TAG, "onTouch: 超出右边空间");
                            l=strip.getRight()-v.getWidth();
                        }else{
                            l=v.getLeft()+ dX;
                        }
                        lastX=nowX;
                        Log.d(TAG, "onTouch: " + nowX);
                        v.layout(l, v.getTop(), l+v.getWidth(), v.getBottom());
                    }
                    if (direction > 0) {
                        //按钮右移
                        postMes(RIGHT);
                    } else {
                        postMes(LEFT);
                    }

                    break;
                case MotionEvent.ACTION_UP:
                    direction=0;//重置direction。
                    postMes(STOP);
                    v.layout(x, v.getTop(), x + v.getWidth(), v.getBottom());
                    v.performClick();
                    break;
            }
            return true;
        }
    }

    /**
     * @param shift 按钮向左或向右的动作，停止时发送stop
     */
    private void postMes(shiftPos shift) {
        if (mPlay != null) {
            mPlay.send(shift);
        }
    }

    public interface Play {
        void send(shiftPos shift);
    }

    public void setOnPlay(Play play) {
        this.mPlay = play;
    }

    public static enum shiftPos {//右移或左移,以及停止
        LEFT, RIGHT, STOP
    }

    public interface changeStripSize {
        void changeSize(View v);
    }

    public void setOnChangeStripSize(changeStripSize changeStripSize) {
        this.changeInterface = changeStripSize;
    }


}
