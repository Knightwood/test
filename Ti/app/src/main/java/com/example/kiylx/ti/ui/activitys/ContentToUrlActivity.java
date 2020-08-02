package com.example.kiylx.ti.ui.activitys;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;

import com.crystal.customview.baseadapter1.BaseAdapter;
import com.crystal.customview.baseadapter1.BaseHolder;
import com.crystal.customview.slider.Slider;
import com.example.kiylx.ti.R;
import com.example.kiylx.ti.mvp.presenter.WebViewManager;
import com.example.kiylx.ti.tool.LogUtil;
import com.example.kiylx.ti.tool.ProcessUrl;
import com.example.kiylx.ti.webview32.nestedjspack.SuggestLiveData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ContentToUrlActivity extends AppCompatActivity {
    private static final String TAG = "ContentToUrlActivity";

    private EditText mTextView;
    private View mSearchToolView;//搜索界面，包括有历史匹配和快捷输入
    private String origin;//传入的字符串，它是现在正在搜索框中显示的
    private SuggestLiveData suggestLiveData;
    private WebViewManager manager;

    /**
     * livedata会持有数据，在第一次获取搜索建议显示后，再次打开此界面时会因为上一次的查询，调用onchange进而触发更新界面而显示上一次的搜索建议。
     * 因此使用此变量标志每次打开此activity后，初始化为false，此时不触发更新界面，当键入内容时，更改为true，获取新的搜索建议列表
     */
    private boolean afterFirstEdit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_to_url);
        initData();
        initView();
    }

    private void initData() {
        manager = WebViewManager.getInstance();
        suggestLiveData = SuggestLiveData.getInstance();
        suggestLiveData.postValue(null);
    }

    private void initView() {
        mTextView = findViewById(R.id.search_content);
        mSearchToolView = findViewById(R.id.viewStub_search_tool2);

        //初始化搜索框显示内容
        origin = getIntent().getStringExtra("text_or_url");
        mTextView.setText(origin);
    }

    @Override
    protected void onStart() {
        super.onStart();
        openSearchEdit();
        exportSuggest(null);//初始化建议列表
        listenSuggest();//监听搜索建议变化，更新界面
    }

    @Override
    protected void onStop() {
        super.onStop();
        afterFirstEdit = false;
        finish();
    }

    /**
     * 打开搜索框
     */
    private void openSearchEdit() {
        //mTextView 搜索框
        //获取焦点并弹出键盘
        mTextView.setFocusableInTouchMode(true);
        mTextView.requestFocus();
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInput(mTextView, 0);

        Slider slider = mSearchToolView.findViewById(R.id.slidrp);
        slider.setOnPlay(new Slider.Play() {
            //接收滑动杆的移动事件，通过Str接口转发。
            @Override
            public void send(Slider.shiftPos shift) {
                LogUtil.d(TAG, "滑动事件: " + shift.toString());
                shiftStr(shift.toString());
            }

        });

        mTextView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    LogUtil.d(TAG, "onKeyDown: ");
                    //监听回车键，按下的时候就开始执行搜索操作。
                    String s = mTextView.getText().toString();
                    if (!s.equals("")) {
                        LogUtil.d(TAG, "onKey: " + s + ":" + ProcessUrl.processString(s, getApplicationContext()));
                        startSearchContent(s);
                    }
                    closeSearchEdit();
                    return true;
                }
                return false;
            }
        });
        mTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (!afterFirstEdit) {
                    //开始编辑文本，此时需要获取搜索建议，因此可以开始更新搜索建议界面
                    afterFirstEdit = true;
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                LogUtil.d(TAG, "afterTextChanged: " + s.toString());
                if (manager != null) {
                    String text = s.toString();
                    if (text != null && !text.equals("")) {
                        manager.getJsManager().exeJsCode(manager.getTop(MainActivity.getCurrent()), text);
                    } else {
                        updateSuggest(null);
                    }
                }
            }
        });
        //透明的view，点击后关掉这个界面
        /*ImageView closeView = findViewById(R.id.image_alpha0);
        closeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeSearchEdit();
            }
        });*/
    }

    /**
     * @param s 待处理的搜索内容字符串
     *          处理输入内容，转化为url，然后结束当前activity，让webview载入网址。
     */
    private void startSearchContent(String s) {
        String result = ProcessUrl.processString(s, getApplicationContext());
        Intent intent = new Intent();
        intent.putExtra("text_or_url", result);
        setResult(RESULT_OK, intent);
        finish();
    }

    /**
     * @param s left,right,stop，向左移动，向右移动，或是停止
     *          移动光标
     */
    public void shiftStr(String s) {
        if (!(s).equals("STOP")) {
            //只要不是STOP，获取输入框字符串长度，获取当前光标位置，然后移动光标
            int endPos = mTextView.length();
            int now = mTextView.getSelectionStart();
            if (s.equals("LEFT") && now > 0) {
                mTextView.setSelection(--now);
            } else {
                if (now < endPos)
                    mTextView.setSelection(++now);
            }
        }
    }

    //以下三个方法，把文字追加到搜索框
    public void writewww(View v) {
        mTextView.append("www.");
    }

    public void writehttp(View v) {
        mTextView.append("http://");
    }

    public void writecom(View v) {
        mTextView.append(".com");
    }

    private void closeIME(View v) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    //关闭搜索框
    private void closeSearchEdit() {
        mTextView.clearFocus();
        mTextView.setFocusableInTouchMode(false);
        closeIME(mTextView);
    }

    //===========================recyclerview
    RecyclerView suggestRecyclerView;
    SuggestAdapter suggestAdapter;

    /**
     * @param list 初始的搜索建议，一开始是空的
     *             初始化recyclerview
     */
    public void exportSuggest(List<String> list) {
        suggestRecyclerView = findViewById(R.id.some_matches);
        suggestRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        suggestAdapter = new SuggestAdapter(list);
        suggestRecyclerView.setAdapter(suggestAdapter);
    }

    private void updateSuggest(List<String> list) {
        LogUtil.d(TAG, "updateSuggest: 更新搜索建议");
        if (suggestAdapter == null) {
            suggestAdapter = new SuggestAdapter(list);
            suggestRecyclerView.setAdapter(suggestAdapter);
        } else {
            suggestAdapter.setData(list);
            suggestAdapter.notifyDataSetChanged();
        }
    }

    public class SuggestAdapter extends BaseAdapter<String, BaseHolder<String>> {

        public SuggestAdapter(List<String> list) {
            super(list);
        }

        @Override
        public BaseHolder<String> createHolder(View v) {
            return new BaseHolder<>(v);
        }

        @Override
        public int itemResId() {
            return R.layout.blackitem;
        }

        @Override
        public void bind(BaseHolder<String> holder, String data) {
            holder.setText(R.id.blacktext, data)
                    .setOnIntemClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startSearchContent(data);
                        }
                    }, data);
        }
    }


    private void listenSuggest() {

        suggestLiveData.observe(this, new Observer<String[]>() {
            @Override
            public void onChanged(String[] result) {
                if (result == null) {
                    updateSuggest(null);
                    LogUtil.d(TAG, "onChanged: livedata建议列表null");
                }
                if (afterFirstEdit && result != null){
                    updateSuggest(Arrays.asList(result));
                    for (int i = 0; i < result.length; i++) {
                        LogUtil.d(TAG, "onChanged: "+result[i]+"\n");
                    }
                    LogUtil.d(TAG, "onChanged: livedata建议列表被改变");
                }

            }
        });
    }
}
