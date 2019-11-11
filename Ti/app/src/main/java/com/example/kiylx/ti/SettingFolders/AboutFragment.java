package com.example.kiylx.ti.SettingFolders;


import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import com.example.kiylx.ti.R;

public class AboutFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.setting_about, rootKey);
    }
}
