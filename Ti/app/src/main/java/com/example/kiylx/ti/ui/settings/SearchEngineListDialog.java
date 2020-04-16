package com.example.kiylx.ti.ui.settings;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.kiylx.ti.R;
import com.example.kiylx.ti.Tool.HashMapProcess;
import com.example.kiylx.ti.Tool.PreferenceTools;
import com.example.kiylx.ti.conf.SomeRes;
import com.example.kiylx.ti.conf.WebviewConf;

import java.util.HashMap;

/**
 * 创建者 kiylx
 * 创建时间 2020/3/20 11:25
 */
public class SearchEngineListDialog extends DialogFragment {
    private static final String TAG = "搜索引擎列表";
    private View rootView;
    private RadioGroup group;
    private TextView textView1;//自定义搜索引擎的textview
    private HashMap<String, String> engineMap;//存放搜索引擎字符串集合的hashMap
    private RadioButton customurlRadio;//自定义搜索引擎的buttom
    private String url = null;


    public static SearchEngineListDialog getInstance() {
        return new SearchEngineListDialog();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        rootView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_searchengine_list, null);
        group = rootView.findViewById(R.id.url_radioGroup);
        textView1 = rootView.findViewById(R.id.customurl);
        customurlRadio = group.findViewById(R.id.customurlradio);

        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {


            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.baidu:
                        url = SomeRes.baidu;
                        break;
                    case R.id.bing:
                        url = SomeRes.bing;
                        break;
                    case R.id.sougou:
                        url = SomeRes.sougou;
                        break;
                    case R.id.google1:
                        url = SomeRes.google;
                        break;
                    case R.id.miji:
                        url = SomeRes.miji;
                        break;
                    case R.id.customurlradio:
                        break;
                }
                if (customurlRadio.isChecked()) {
                    textView1.setVisibility(View.VISIBLE);
                } else {
                    textView1.setVisibility(View.INVISIBLE);
                }

                Log.d(TAG, "onClick: " + url);
            }
        });

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity()).setTitle("搜索引擎");
        dialogBuilder.setView(rootView).setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //记录下自定义的搜索引擎，虽然它可能没有设置为默认值
                PreferenceTools.putString(getActivity(), WebviewConf.customsearchengine, textView1.getText().toString());
                if (group.getCheckedRadioButtonId() == R.id.customurlradio) {
                    /*如果选择了其他的选项，url在上面就被赋值了。
                    如果选择了自定义的那一项，url还没有被赋值，应该在这里获取textview的值赋值给url*/
                    url = textView1.getText().toString();
                }
                PreferenceTools.putString(getActivity(), WebviewConf.searchengine, url);
                setResult(url);//把默认搜索引擎字符串传回目标fragment
            }
        });

        Log.d(TAG, "onCreateDialog: 对话框显示");

        return dialogBuilder.create();
    }

    private void setResult(String url) {
        if (getTargetFragment() == null) {
            return;
        }
        Intent intent = new Intent();
        intent.putExtra(WebviewConf.searchengine, url);
        getTargetFragment().onActivityResult(0, Activity.RESULT_OK, intent);
    }


    @Override
    public void onStart() {
        super.onStart();

        Dialog dialog = getDialog();
        if (dialog != null && dialog.getWindow() != null) {
            Window window = dialog.getWindow();
            WindowManager.LayoutParams layoutParams = window.getAttributes();
            //指定显示位置
            //layoutParams.gravity = Gravity.BOTTOM;
            //指定显示大小
            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            //设置背景，不然无法扩展到屏幕边缘
            window.setBackgroundDrawable(new ColorDrawable(Color.rgb(255, 255, 255)));
            //显示消失动画
            window.setWindowAnimations(R.style.animate_dialog);
            //让属性设置生效
            window.setAttributes(layoutParams);
            //设置点击外部可以取消对话框
            setCancelable(true);
        }

        String defaultEngine = PreferenceTools.getString(getActivity(), WebviewConf.searchengine);
        engineMap = PreferenceTools.getHashMap2(getActivity(), WebviewConf.searchengineList);
        RadioButton dario = null;

        if (engineMap.containsValue(defaultEngine)) {//如果默认搜索引擎是内置的5个之一，把相应的radiobuttom打上勾
            String defName = HashMapProcess.getKeyOfValues(engineMap, defaultEngine);//获取默认搜索引擎在hashmap中对应的key值
            switch (defName) {
                case "百度":
                    dario = group.findViewById(R.id.baidu);
                    break;
                case "必应":
                    dario = group.findViewById(R.id.bing);
                    break;
                case "搜狗":
                    dario = group.findViewById(R.id.sougou);
                    break;
                case "秘迹搜索":
                    dario = group.findViewById(R.id.miji);
                    break;
                case "谷歌搜索":
                    dario = group.findViewById(R.id.google1);
                    break;
            }
            if (dario != null) {
                dario.setChecked(true);
                textView1.setVisibility(View.INVISIBLE);
            }

        } else {//否则，把自定义的那一项打上勾
            customurlRadio.setChecked(true);
            textView1.setVisibility(View.VISIBLE);
        }
        //那个文本编辑框，即使上次写好的自定义值不是默认值，也要拿到并写进textview
        textView1.setText(PreferenceTools.getString(getActivity(), WebviewConf.customsearchengine, ""));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
