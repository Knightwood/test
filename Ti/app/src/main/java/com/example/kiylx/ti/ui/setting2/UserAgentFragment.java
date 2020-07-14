package com.example.kiylx.ti.ui.setting2;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.kiylx.ti.R;
import com.example.kiylx.ti.conf.WebviewConf;
import com.example.kiylx.ti.tool.HashMapProcess;
import com.example.kiylx.ti.tool.preferences.PreferenceTools;

import java.util.HashMap;

/**
 * 创建者 kiylx
 * 创建时间 2020/4/29 17:50
 */
public class UserAgentFragment extends Fragment {
    private static final String TAG = "userAgent列表";
    private View rootView;
    private RadioGroup group;
    private TextView textView1;//自定义搜索引擎的textview
    private HashMap<String, String> agentMap;//存放搜索引擎字符串集合的hashMap
    private RadioButton customurlRadio;//自定义搜索引擎的buttom
    private String useragent = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_useragent_list, null);
        group = rootView.findViewById(R.id.agent_radioGroup);
        textView1 = rootView.findViewById(R.id.customagentText);
        customurlRadio = group.findViewById(R.id.customagentradio);
        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {


            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.defaultAgent:
                        useragent=null;
                        break;
                    case R.id.chrome:
                        useragent = getString(R.string.Chrome_agent);
                        break;
                    case R.id.firefox:
                        useragent = getString(R.string.FireFox_agent);
                        break;
                    case R.id.IE:
                        useragent = getString(R.string.IE9_agent);
                        break;
                    case R.id.iPHONE:
                        useragent = getString(R.string.iPhone_agent);
                        break;
                    case R.id.customurlradio:
                        break;
                }
                if (customurlRadio.isChecked()) {
                    textView1.setVisibility(View.VISIBLE);
                } else {
                    textView1.setVisibility(View.INVISIBLE);
                }

                Log.d(TAG, "onClick: " + useragent);
            }
        });


        return rootView;

    }


    @Override
    public void onStart() {
        super.onStart();

        String defaultAgentStr = PreferenceTools.getString(getActivity(), WebviewConf.userAgent);//useragent默认值
        agentMap = PreferenceTools.getHashMap2(getActivity(), WebviewConf.userAgentList);//存储所有useragent的hashmap
        RadioButton dario = null;

        if (agentMap.containsValue(defaultAgentStr)) {//如果默认useragent是内置的几个之一，把相应的radiobuttom打上勾
            String defName = HashMapProcess.getKeyOfValues(agentMap, defaultAgentStr);//获取默认搜索引擎在hashmap中对应的key值
            switch (defName) {
                case "Chrome_agent":
                    dario = group.findViewById(R.id.chrome);
                    break;
                case "FireFox_agent":
                    dario = group.findViewById(R.id.firefox);
                    break;
                case "IE9_agent":
                    dario = group.findViewById(R.id.IE);
                    break;
                case "iPhone_agent":
                    dario = group.findViewById(R.id.iPHONE);
                    break;
                case "默认_agent":
                    dario = group.findViewById(R.id.defaultAgent);
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
        textView1.setText(PreferenceTools.getString(getActivity(), WebviewConf.customAgent, ""));
    }

    @Override
    public void onStop() {
        super.onStop();
        //记录下自定义的浏览器标识，虽然它可能没有设置为默认值
        PreferenceTools.putString(getActivity(), WebviewConf.customAgent, textView1.getText().toString());
        if (group.getCheckedRadioButtonId() == R.id.customurlradio) {
                    /*如果选择了其他的选项，默认agent在上面那一句就被赋值了。
                    如果选择了自定义的那一项，默认agent还没有被赋值，应该在这里获取textview的值赋值给agent*/
            useragent = textView1.getText().toString();
        }
        //把默认值写入sharedPreference
        PreferenceTools.putString(getActivity(), WebviewConf.userAgent, useragent);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
