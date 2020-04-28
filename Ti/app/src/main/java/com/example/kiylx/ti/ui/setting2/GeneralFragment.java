package com.example.kiylx.ti.ui.setting2;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreference;

import com.example.kiylx.ti.R;

/**
 * 创建者 kiylx
 * 创建时间 2020/4/27 23:14
 */
public class GeneralFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener{
    public static Fragment newInstance() {
        return new GeneralFragment();
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.setting_general, rootKey);
    initPreference();
    }

    private void initPreference() {
        final SwitchPreference defaultHomePage=findPreference("home_page");
        defaultHomePage.setOnPreferenceChangeListener(this);
        final EditTextPreference customHomePage=findPreference("homepage_url");

    }


    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        switch (preference.getKey()){
            case "home_page":
                if ((boolean)newValue){

                }
        }
        return false;
    }
}
