package com.example.kiylx.ti.settingFolders;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.preference.ListPreference;
import androidx.preference.PreferenceFragmentCompat;

import com.example.kiylx.ti.R;
import com.example.kiylx.ti.livedata.DefaultValue;
import com.example.kiylx.ti.livedata.DefaultValueLiveData;

/**
 * 常规的设置
 */
public class ConventionalFragment extends PreferenceFragmentCompat {
    private SharedPreferences.OnSharedPreferenceChangeListener listener;
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.setting_conventional, rootKey);

        //设置默认值
        final ListPreference userAgent=findPreference("explorer_flags");
        assert userAgent != null;
        //userAgent.setValueIndex(4);

        //更新liveData
        listener=new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                DefaultValueLiveData.getInstance().setmLiveData (new DefaultValue(userAgent.getValue()));
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
}
