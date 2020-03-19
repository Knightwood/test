package com.example.kiylx.ti.settingFolders;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import com.example.kiylx.ti.Discard.DefaultValue_1;
import com.example.kiylx.ti.Discard.LiveData_DF_WebView;

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
