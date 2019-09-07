package com.example.kiylx.ti.settingFolders;


import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import com.example.kiylx.ti.R;

public class SecondFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.second, rootKey);
    }
}
