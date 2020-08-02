package com.example.kiylx.ti.ui.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.crystal.customview.slider.Slider;
import com.example.kiylx.ti.R;
import com.example.kiylx.ti.tool.LogUtil;

/**
 * 创建者 kiylx
 * 创建时间 2020/4/21 9:29
 */
public class SearchToolFragment extends Fragment {
    private static final String TAG = "搜索工具页面";
    private static final String ARG_PARAM1 = "URL";
    private String food;//传入的url，用于匹配记录
    private SendMesStr mStr;

    private View mRoot;


    public static SearchToolFragment newInstance(String url) {
        Bundle bundle = new Bundle();
        bundle.putString(ARG_PARAM1, url);
        SearchToolFragment fragment = new SearchToolFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            food = getArguments().getString(ARG_PARAM1);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRoot = inflater.inflate(R.layout.search_tool, container);
        mRoot.setOnClickListener(new View.OnClickListener() {
            String s;

            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.http_button:
                        s = "http://";
                        break;
                    case R.id.com_button:
                        s = ".com";
                        break;
                    case R.id.www_button:
                        s = "www.";
                        break;
                }
                LogUtil.d(TAG, "点击文本: "+s);
                if (mStr != null) {
                    mStr.sendText(s);
                }
            }
        });

        Slider slider = mRoot.findViewById(R.id.slidrp);
        slider.setOnPlay(new Slider.Play() {
            //接收滑动杆的移动事件，通过Str接口转发。
            @Override
            public void send(Slider.shiftPos shift) {
                LogUtil.d(TAG, "滑动事件: "+shift.toString());
                if (mStr != null) mStr.sendPos(shift);
            }
        });
        return mRoot;
    }

    public interface SendMesStr {
        void sendText(String s);

        void sendPos(Slider.shiftPos pos);
    }

    public void setStr(SendMesStr mStr) {
        this.mStr = mStr;
    }


}
