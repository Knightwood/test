package com.example.kiylx.ti.settingFolders;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.preference.CheckBoxPreference;
import androidx.preference.ListPreference;
import androidx.preference.PreferenceFragmentCompat;

import com.example.kiylx.ti.R;
import com.example.kiylx.ti.livedata.DefaultValue_WebView;
import com.example.kiylx.ti.livedata.LiveData_DF_WebView;

/**
 * 常规的设置
 */
public class ConventionalFragment extends PreferenceFragmentCompat {
    private SharedPreferences.OnSharedPreferenceChangeListener listener;//sharedpreference监听器
    private DefaultValue_WebView defaultValue;
     private ListPreference userAgent;
     private CheckBoxPreference customDownloadTool;
     private ListPreference textZoomlist;


    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.setting_conventional, rootKey);
        //设置默认值
        userAgent=findPreference("explorer_flags");
        customDownloadTool=findPreference("downloadTools");
        textZoomlist=findPreference("textZoomlist");

        assert userAgent != null;
        defaultValue=new DefaultValue_WebView(userAgent.getValue());


        //更新liveData
        listener=new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                Log.d("浏览器标识", "获取浏览器标识: " + sharedPreferences.getString ("textZoomlist","125"));
                LiveData_DF_WebView.getInstance().setmLiveData (changeValue());
            }
        };

        //getPreferenceManager().getSharedPreferences()是用来获取sharedpreference
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(listener);

    }

    /**
     * @return 设置默认值对象的属性
     */
    private DefaultValue_WebView changeValue() {
        defaultValue.setUser_agent(userAgent.getValue());
        defaultValue.setUseCustomDwnloadTool(customDownloadTool.isChecked());
        defaultValue.setTextZoom(Integer.valueOf(textZoomlist.getValue()));
        return defaultValue;
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(listener);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(listener);
    }
}
