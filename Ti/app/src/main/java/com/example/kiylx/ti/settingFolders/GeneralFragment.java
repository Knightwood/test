package com.example.kiylx.ti.settingFolders;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.example.kiylx.ti.R;
import com.example.kiylx.ti.Tool.HashMapProcess;
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

    private ArrayAdapter<String> adapter2;//TextZoom的Spinner
    private HashMap<String, Integer> textZoomMap;//存放textZoom集合的hashMap
    private List<String> zoomNamelist;//字体缩放hashMap的key值列表

    public GeneralFragment() {
    }


    public static GeneralFragment newInstance() {
        GeneralFragment fragment = new GeneralFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUserAgentMap();//读取userAgent列表的HashMap
        initTextZoomHashMap();//读取字体缩放的HashMap
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
        homePageUrl.setVisibility(useCustomtHomePage ? View.VISIBLE : View.GONE);
        //设置自定义主页文本框的网址
        homePageUrl.setText(PreferenceTools.getString(getActivity(), WebviewConf.homepageurl));
        //监听开关的状态，如果使用自定义主页，让自定义主页编辑框显示出来。
        homeButtom.setOnCheckedChangeListener(checkedChangeListener);

        homePageUrl.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                //把获取的自定义主页网址写入preference
                PreferenceTools.putString(getActivity(), WebviewConf.homepageurl, s.toString());
            }
        });


        //useragent的spinner
        userAgentSpinner = f(R.id.spinner2);
        initUserAgentSpinner();

        //搜索引擎文本框，点击它跳转到设置搜索引擎的页面
        searchEngineText = f(R.id.searchengine_buttom);
        searchEngineText.setOnClickListener(clickListener);

        //清除数据按钮
        cleanDataBottom = f(R.id.cleanData);
        cleanDataBottom.setOnClickListener(clickListener);

        //文本缩放spinner
        textZoomSpinner = f(R.id.text_zoom_buttom);
        initTextZoomSpinner();

        //打开浏览器要不要恢复上次的网址开关
        resumeDataBottom = f(R.id.resumeData);
        resumeDataBottom.setChecked(PreferenceTools.getBoolean(getActivity(), WebviewConf.resumeData));
        resumeDataBottom.setOnCheckedChangeListener(checkedChangeListener);

        //用不用内置的下载工具
        customDownloadBottom = f(R.id.downloadtool);
        customDownloadBottom.setChecked(PreferenceTools.getBoolean(getActivity(), WebviewConf.customDownload));
        customDownloadBottom.setOnCheckedChangeListener(checkedChangeListener);
    }

    private CompoundButton.OnCheckedChangeListener checkedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            switch (buttonView.getId()) {
                case R.id.homepage:
                    PreferenceTools.putBoolean(getActivity(), WebviewConf.useCustomHomepage, isChecked);
                    homePageUrl.setVisibility(isChecked ? View.VISIBLE : View.GONE);
                    break;
                case R.id.resumeData:
                    PreferenceTools.putBoolean(getActivity(), WebviewConf.resumeData, isChecked);
                    break;
                case R.id.downloadtool:
                    PreferenceTools.putBoolean(getActivity(), WebviewConf.customDownload, isChecked);
                    break;

            }
        }
    };

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.searchengine_buttom:
                    SearchEngineSetting_Fragment fragment=new SearchEngineSetting_Fragment();
                    FragmentManager fm=getFragmentManager();
                    fragment.show(fm,"searchEngineListDialog");
                    break;
                case R.id.cleanData:
                    break;
            }
        }
    };


    /**
     * 获取useragent的hashmap，然后遍历hashmap，把key装进List中，spinner显示key值。
     * <p>
     * 在useragent的spinner中，显示的是key值，点击某一个选项时把相应的value值写入默认useragent的preference
     */
    private void initUserAgentMap() {

        userAgentMap = new HashMap<>(Objects.requireNonNull(PreferenceTools.getHashMap2(Objects.requireNonNull(getActivity()), WebviewConf.userAgentList)));
        useragentNamelist = HashMapProcess.getKeys(userAgentMap);
    }

    /**
     * userAgent是由key和value组成的键值对，key是显示名称，value是对应的useragent字符串值。
     * 例如：key（名称）："IE 9.0", value（值）："Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; Trident/5.0"
     * <p>
     * 然后这几个userAgent放入一个HashMap（M），在第一次打开应用时被初始化，把HashMap写入preference（P）。
     * 并且，设置一个单独的preference（A）存储默认userAgent。
     * <p>
     * 在这个fragment中，读取“P”，取出HashMap，让spinner显示key值，点击其中一项，把key值对应的value值写入"A"，在new Webview时直接读取“A”里的字符串值。
     * <p>
     * 默认的userAgent键值对是："默认", null。
     * 在启动页初始化时就将这个默认值写入"A"，在“M”中也有一项与默认userAgent键值对一样，这样在spinner中选择默认时就不会出错。
     */
    private void initUserAgentSpinner() {
        int pos;
        if (adapter1 == null) {

            adapter1 = new ArrayAdapter<>(Objects.requireNonNull(getActivity()), android.R.layout.simple_spinner_item, useragentNamelist);
            adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        } else {
            adapter1.notifyDataSetChanged();
        }
        //获取默认userAgent字符串在hashmap中的位置，让spinner显示这个value对应的key名称
        pos = HashMapProcess.getValuePos(userAgentMap, PreferenceTools.getString(getActivity(), WebviewConf.userAgent));

        userAgentSpinner.setAdapter(adapter1);
        userAgentSpinner.setSelection(pos);//设置显示名称

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

    private void initTextZoomHashMap() {
        textZoomMap = new HashMap<>(Objects.requireNonNull(PreferenceTools.getHashMap(getActivity(), WebviewConf.textZoomList)));
        zoomNamelist = HashMapProcess.getKeys(textZoomMap);
    }

    private void initTextZoomSpinner() {
        int pos;
        if (adapter2 == null) {

            //spinner显示的所有名称（hashmap的key值）
            adapter2 = new ArrayAdapter<>(Objects.requireNonNull(getActivity()), android.R.layout.simple_spinner_item, zoomNamelist);
            adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        } else {
            adapter2.notifyDataSetChanged();
        }
        //获取默认字体缩放值在hashmap中的位置，让spinner显示这个value对应的key名称
        pos = HashMapProcess.getValuePos(textZoomMap, PreferenceTools.getInt(getActivity(), WebviewConf.textZoom));

        textZoomSpinner.setAdapter(adapter2);
        textZoomSpinner.setSelection(pos);//设置显示名称

        textZoomSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //设置字体缩放的偏好值为：从spinner选择一项key，把这项在hashmap中对应的value值写入preference
                PreferenceTools.putInt(getActivity(), WebviewConf.textZoom, (int) textZoomMap.get(zoomNamelist.get(position)));
                Log.d(TAG, "字体缩放值: " + textZoomMap.get(zoomNamelist.get(position)));
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

    /**
     * @param resId 资源id
     * @param <T> 继承自View的泛型
     * @return 返回View类型的，通过id获取的view
     */
    private <T extends View> T f(int resId) {
        return (T) rootview.findViewById(resId);
    }
}
