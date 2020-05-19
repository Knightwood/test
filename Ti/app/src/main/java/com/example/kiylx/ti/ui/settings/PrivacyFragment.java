package com.example.kiylx.ti.ui.settings;

import android.app.Application;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.example.kiylx.ti.R;
import com.example.kiylx.ti.Xapplication;

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
                xapplication.setDNT((boolean)newValue);
                break;
        }
        return true;
    }
}
