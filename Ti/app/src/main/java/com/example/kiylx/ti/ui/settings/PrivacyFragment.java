package com.example.kiylx.ti.ui.settings;

import android.os.Bundle;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.example.kiylx.ti.R;
import com.example.kiylx.ti.xapplication.Xapplication;

/**
 * 隐私设置
 */
public class PrivacyFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.setting_privacy,rootKey);

    }


    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        switch (preference.getKey()){
            case "dont_track":
                Xapplication xapplication= (Xapplication) Xapplication.getInstance();
                xapplication.getStateManager().setDNT((boolean)newValue);
                break;
        }
        return true;
    }
}
