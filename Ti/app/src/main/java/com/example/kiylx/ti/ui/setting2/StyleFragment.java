package com.example.kiylx.ti.ui.setting2;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import com.example.kiylx.ti.R;

/**
 * 创建者 kiylx
 * 创建时间 2020/4/29 17:44
 */
public class StyleFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.setting_theme,rootKey);
    }
}
