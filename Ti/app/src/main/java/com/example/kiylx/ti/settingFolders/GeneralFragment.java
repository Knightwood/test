package com.example.kiylx.ti.settingFolders;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.example.kiylx.ti.R;
import com.example.kiylx.ti.conf.PreferenceTools;
import com.example.kiylx.ti.conf.WebviewConf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;


public class GeneralFragment extends Fragment {
    private static final String TAG = "设置测试";
    private View rootview;
    private Switch homeButtom;
    private EditText homePageUrl;
    private Spinner userAgentSpinner;
    private TextView searchEngineText;
    private TextView cleanDataBottom;
    private Spinner textZoomSpinner;
    private Switch resumeDataBottom;
    private Switch customDownloadBottom;
    private ArrayAdapter<String> adapter1;//userAgent的Spinner
    private HashMap<String, String> userAgentMap;//存放userAgent的hashMap
    private List<String> useragentNamelist;
    private List<String> useragentList;

    public GeneralFragment() {
    }


    public static GeneralFragment newInstance() {
        GeneralFragment fragment = new GeneralFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUserAgentMap();
    }

    /**
     * 获取useragent的hashmap，然后遍历hashmap，把key装进List中，spinner显示key值。
     * <p>
     * 在useragent的spinner中，显示的是key值，点击某一个选项时把相应的value值写入默认useragent的preference
     */
    private void initUserAgentMap() {

        userAgentMap = new HashMap<>(Objects.requireNonNull(PreferenceTools.getHashMap2(Objects.requireNonNull(getActivity()), WebviewConf.userAgentList)));
        Set<String> useragentNameSet = userAgentMap.keySet();//hashmap的key值集合
        Iterator<String> userAgentNameit = useragentNameSet.iterator();

        useragentNamelist = new ArrayList<>();
        while (userAgentNameit.hasNext()) {
            String key = userAgentNameit.next();
            useragentNameSet.add(key);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootview = inflater.inflate(R.layout.fragment_general, container, false);
        initView();
        return rootview;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    /**
     * 初始化视图，初始化那些设置选项以及监听
     */
    private void initView() {
        //用不用自定义主页网址
        boolean useCustomtHomePage = PreferenceTools.getBoolean(getActivity(), WebviewConf.useCustomHomepage);

        //用不用自定义主页网址的开关
        homeButtom = f(R.id.homepage);
        homeButtom.setChecked(useCustomtHomePage);

        //如果使用自定义主页网址，自定义主页网址的文本框显示出来，反之，隐藏文本框
        homePageUrl = f(R.id.homepage_url_view);
        homeButtom.setVisibility(useCustomtHomePage ? View.VISIBLE : View.GONE);
        //设置自定义主页文本框的网址
        homePageUrl.setText(PreferenceTools.getString(getActivity(), WebviewConf.homepageurl));

        //useragent的spinner
        userAgentSpinner = f(R.id.spinner2);
        initUserAgentSpinner();

        //搜索引擎文本框，点击它跳转到设置搜索引擎的页面
        searchEngineText = f(R.id.searchengine_buttom);

        //清除数据按钮
        cleanDataBottom = f(R.id.cleanData);

        //文本缩放spinner
        textZoomSpinner = f(R.id.text_zoom_buttom);

        //打开浏览器要不要恢复上次的网址开关
        resumeDataBottom = f(R.id.resumeData);

        //用不用内置的下载工具
        customDownloadBottom = f(R.id.downloadtool);
    }

    private void initUserAgentSpinner() {
        int pos;
        if (adapter1 == null) {

            adapter1 = new ArrayAdapter<>(Objects.requireNonNull(getActivity()), android.R.layout.simple_spinner_item, useragentNamelist);
            adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        } else {
            adapter1.notifyDataSetChanged();
        }
        //获取默认userAgent字符串在hashmap中的位置，让spinner显示这个名称

        pos = useragentNamelist.indexOf(PreferenceTools.getString(getActivity(), WebviewConf.userAgent));
        userAgentSpinner.setAdapter(adapter1);
        //把spinner显示的设置为上面拿到的pos位置的useragnet名称
        userAgentSpinner.setSelection(pos);

        userAgentSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //设置useragent的偏好值为：从spinner选择一项，把这项的名称在hashmap中对应的value值写入preference
                PreferenceTools.putString(getActivity(), WebviewConf.userAgent, userAgentMap.get(useragentNamelist.get(position)));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

    private <T extends View> T f(int resId) {
        return (T) rootview.findViewById(resId);
    }
}
