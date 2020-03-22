package com.example.kiylx.ti.settingFolders;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import com.example.kiylx.ti.Trash.DefaultValue_1;
import com.example.kiylx.ti.Trash.LiveData_DF_WebView;

/**
 * @创建者 kiylx
 * @创建时间 2020/2/27 18:03
 */
public abstract class BaseSetFragment extends PreferenceFragmentCompat {
    private SharedPreferences.OnSharedPreferenceChangeListener listener;//sharedpreference监听器
    private DefaultValue_1 value;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        initValue();
//更新liveData
        listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                LiveData_DF_WebView.getInstance().setmLiveData(defaultvalue());
            }
        };

        /*观察数据示例，
        这个livedata是自定义的，生命周期随application，所以可以跨组件通讯。
        这个livedata是继承自livedata，不是继承自viewmodel。viewmodel是生命周期组件，livedata是观察者模式
        final Observer<DefaultValue_1> observer = new Observer<DefaultValue_1>() {
            @Override
            public void onChanged(DefaultValue_1 s) {
                Log.d(TAG, "获取浏览器标识: " + s.getUser_agent());
                mWebViewManager.setValue(s);
            }
        };

        LiveData_DF_WebView.getInstance().observe(this, observer);*/


        /*
         * getPreferenceManager().getSharedPreferences()获取sharedpreference
         * */
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(listener);
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

    public abstract void initValue();

    public abstract DefaultValue_1 defaultvalue();
}
