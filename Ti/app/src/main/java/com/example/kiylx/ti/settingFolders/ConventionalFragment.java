package com.example.kiylx.ti.settingFolders;

import android.os.Bundle;

import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.example.kiylx.ti.R;

/**
 * 常规的设置
 */
public class ConventionalFragment extends PreferenceFragmentCompat {
    private String user_agent=null;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.setting_conventional, rootKey);
    }


}
