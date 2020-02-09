package com.crystal.preferencetest;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

/**
 * @创建者 kiylx
 * @创建时间 2020/2/9{TIME}
 */
public class PreferenceFragment_2 extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.preference_2);
    }
}
