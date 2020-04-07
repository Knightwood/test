package com.crystal.customview.numberPickerView;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.crystal.customview.R;

/**
 * 创建者 kiylx
 * 创建时间 2020/4/3 11:08
 */
public class numberPicker extends LinearLayout implements View.OnClickListener, TextWatcher {
    //最大最小数值,以及没想好干什么用的限制值,限制值在最大和最小值之间
    private int maxValue = Integer.MAX_VALUE;
    private int minValue = 0;
    private int limitValue = maxValue;
    /**
     * 指示当前的值
     */
    private int currentValue = minValue;

    private int defTextSize = 18;
    private LinearLayout mRoot;
    private EditText numText;
    private TextView addView;
    private TextView subView;

    // 监听事件(负责警戒值回调)
    private OnClickInputListener onClickInputListener;
    // 监听输入框内容变化
    private OnInputNumberListener onInputNumberListener;


    public numberPicker(Context context) {
        super(context);
         LayoutInflater.from(context).inflate(R.layout.number_picker, this);
    }

    public numberPicker(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
         LayoutInflater.from(context).inflate(R.layout.number_picker, this);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        //LayoutInflater.from(context).inflate(R.layout.number_picker, this);

        mRoot = (LinearLayout) findViewById(R.id.root_layout);
        subView = f(R.id.sub_button);
        addView = f(R.id.add_button);
        numText = f(R.id.num_text);

        subView.setOnClickListener(this);
        addView.setOnClickListener(this);
        numText.addTextChangedListener(this);

        //自定义属性
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.numberPicker);
        //layout的水平分割线
        Drawable divider = typedArray.getDrawable(R.styleable.numberPicker_individer);
        //控件的背景
        int layout_bg = typedArray.getResourceId(R.styleable.numberPicker_backgroud, R.drawable.bg_number_picker_stroke);
        int addView_bg = typedArray.getResourceId(R.styleable.numberPicker_addBackground, R.drawable.bg_button_add);
        int subView_bg = typedArray.getResourceId(R.styleable.numberPicker_subBackground,R.drawable.bg_button_sub);
        //最大最小值和限制值

        int min = typedArray.getInteger(R.styleable.numberPicker_minValue, 0);
        int max = typedArray.getInteger(R.styleable.numberPicker_maxValue, 10);
        setDefValue(min, max);

        //文本编辑框能否编辑
        boolean canEdit = typedArray.getBoolean(R.styleable.numberPicker_editable, true);
        //编辑框的宽度
        final int editViewWidth = typedArray.getDimensionPixelSize(R.styleable.numberPicker_editextWidth, -1);
        //加减按钮的宽度
        final int buttonViewWidth = typedArray.getDimensionPixelSize(R.styleable.numberPicker_buttonWidth, -1);
        //加减文本的颜色
        int textColor = typedArray.getColor(R.styleable.numberPicker_textColor, 0xff000000);
        //加减和中间文本的字体大小
        int textSize = typedArray.getDimensionPixelSize(R.styleable.numberPicker_textSize, -1);


        //设置上自定义的属性
        mRoot.setBackgroundResource(layout_bg);
        mRoot.setDividerDrawable(divider);

        subView.setBackgroundResource(subView_bg);
        addView.setBackgroundResource(addView_bg);
        subView.setTextColor(textColor);
        addView.setTextColor(textColor);

        //文本框是否可编辑
        if (canEdit) {
            this.numText.setFocusable(true);
            this.numText.setKeyListener(new DigitsKeyListener());
            //setKeyListener： 分配一个数字键监听器,它接受ASCII数字0到9。
        } else {
            this.numText.setFocusable(false);
            this.numText.setKeyListener(null);
        }

        if (textSize > 0) {
            addView.setTextSize(pxTosp(context, textSize));
            subView.setTextSize(pxTosp(context, textSize));
            this.numText.setTextSize(pxTosp(context, textSize));
        } else {
            addView.setTextSize(defTextSize);
            subView.setTextSize(defTextSize);
            this.numText.setTextSize(defTextSize);
        }
        //宽度加减按钮设置，防止和高度不协调
        if (buttonViewWidth > 0) {
            LayoutParams lp = new LayoutParams(buttonViewWidth, LayoutParams.MATCH_PARENT);
            addView.setLayoutParams(lp);
            subView.setLayoutParams(lp);
        }
        //设置文本输入框的宽度
        if (editViewWidth > 0) {
            LayoutParams lp = new LayoutParams(editViewWidth, LayoutParams.MATCH_PARENT);
            this.numText.setLayoutParams(lp);
        }

//必须调用这个，因为自定义View会随着Activity创建频繁的创建array
        //回收资源
        typedArray.recycle();
    }

    /**
     * 初始化默认值
     *
     * @param min 最小值
     * @param max 最大值
     */
    public void setDefValue(int min, int max) {
        this.currentValue = min;
        this.maxValue = max;
        this.minValue = min;
        this.limitValue = max;
        this.numText.setText(String.valueOf(min));

    }


    /**
     * 将dip或dp值转换为px值，保证尺寸大小不变
     *
     * @param dipValue DisplayMetrics类中属性density）
     * @return
     */
    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param spValue （DisplayMetrics类中属性scaledDensity）
     * @return
     */
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * 将px值转换为sp值，保证文字大小不变
     *
     * @param pxValue （DisplayMetrics类中属性scaledDensity）
     * @return
     */
    public static int pxTosp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * 设置最大值和最小值
     *
     * @param value
     */
    public numberPicker setMaxValue(int value) {
        this.maxValue = value;

        return this;
    }

    public numberPicker setMinValue(int value) {
        this.minValue = value;
        return this;
    }

    public numberPicker setLimitValue(int value) {
        if (value > maxValue || value < minValue) {
            this.limitValue = maxValue;
        } else {
            this.limitValue = value;
        }
        return this;
    }

    //获取最大和最小值
    public int getMaxValue() {
        return maxValue;
    }

    public int getMinValue() {
        return minValue;
    }

    public int getLimitValue() {
        return limitValue;
    }

    /**
     * 获取文本框里的数字
     *
     * @return
     */
    public int getNum() {
        try {
            String str = numText.getText().toString().trim();
            return Integer.parseInt(str);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            numText.setText(String.valueOf(minValue) );
            return minValue;
        }

    }

    //设置文本框数值,超出大小值限制则设置一个默认值
    public numberPicker setNum(int value) {
        if (value < minValue) {
            numText.setText(String.valueOf(minValue));
        } else if (value > maxValue) {
            numText.setText(String.valueOf(maxValue));
        } else {
            numText.setText(String.valueOf(value));
        }
        return this;
    }

    //onclick用于监听“加，减”视图的点击
    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.add_button) {
            if (currentValue < maxValue) {
                currentValue++;
                numText.setText(String.valueOf(currentValue));
            }
        } else if (id == R.id.sub_button) {
            if (currentValue > minValue) {
                currentValue--;
                numText.setText(String.valueOf(currentValue));
            }

        }

    }

    //textWatcher,用于监听文本框的输入
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        if (onInputNumberListener != null) {
            onInputNumberListener.beforeTextChanged(s, start, count, after);
        }
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (onInputNumberListener != null) {
            onInputNumberListener.onTextChanged(s, start, before, count);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (onInputNumberListener != null) {
            onInputNumberListener.afterTextChanged(s);
        }
        numText.removeTextChangedListener(this);
        String inputText = s.toString().trim();
        if (!inputText.isEmpty()) {
            int now=Integer.parseInt(inputText);
            setNum(now);
        }else{
            setNum(currentValue);
        }

        numText.addTextChangedListener(this);
        numText.setSelection(numText.getText().toString().length());//让光标在最后面
    }


    public void setOnClickInputListener(OnClickInputListener mOnWarnListener) {
        this.onClickInputListener = mOnWarnListener;
    }

    public void setOnInputNumberListener(OnInputNumberListener onInputNumberListener) {
        this.onInputNumberListener = onInputNumberListener;
    }


    /**
     * 超过警戒值回调
     */
    public interface OnClickInputListener {
        void onWarningForInventory(int inventory);

        void onWarningMinInput(int minValue);

        void onWarningMaxInput(int maxValue);
    }

    /**
     * 输入框数字内容监听
     */
    public interface OnInputNumberListener {

        void beforeTextChanged(CharSequence s, int start, int count, int after);

        void onTextChanged(CharSequence charSequence, int start, int before, int count);

        void afterTextChanged(Editable editable);
    }

    private <T extends View> T f(int resId) {
        return (T) mRoot.findViewById(resId);
    }

}
