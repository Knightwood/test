package com.crystal.customview.Slider;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
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
    private ConstraintLayout mRoot;
    private ImageView sliderButton;
    private ImageView strip;
    private int x;//vbuttonView的x位置
    private int stripR;//滑动条的左坐标
    private int stripL;//滑动条的右坐标
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
        mRoot = (ConstraintLayout) findViewById(R.id.slider_root);
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
        moveButton();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        x = sliderButton.getLeft();
        stripL = strip.getLeft();
        stripR = strip.getRight();

    }

    @SuppressLint("ClickableViewAccessibility")
    private void moveButton() {
        sliderButton.setOnTouchListener(new MoveView());
    }

    private class MoveView implements View.OnTouchListener {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (changeInterface != null) {
                        changeInterface.changeSize(strip);
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    int nowX = (int) event.getRawX();
                    int resultX = nowX;
                    if (nowX < stripL) {
                        resultX = stripL;
                    } else if (nowX > stripR) {
                        resultX = stripR - v.getWidth();
                    }
                    if ((nowX - x) > 0) {
                        //按钮右移
                        postMes(RIGHT);
                    } else {
                        postMes(LEFT);
                    }
                    v.layout(resultX, v.getTop(), resultX + v.getWidth(), v.getBottom());
                    break;
                case MotionEvent.ACTION_UP:
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
