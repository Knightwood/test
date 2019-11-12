package com.example.kiylx.ti.settingFolders;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import com.example.kiylx.ti.R;

public class ConventionalFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.setting_conventional, rootKey);
    }
}
