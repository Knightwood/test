package com.example.kiylx.ti.ui.setting2;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceFragmentCompat;

import com.example.kiylx.ti.R;

/**
 * 创建者 kiylx
 * 创建时间 2020/4/29 17:11
 */
public class MainSettingFragment extends PreferenceFragmentCompat {
    public static MainSettingFragment newInstance() {
        return new MainSettingFragment();
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.setting_main,rootKey);
    }
}
