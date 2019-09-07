package com.example.kiylx.ti.settingFolders;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.example.kiylx.ti.Activitys.MainActivity;
import com.example.kiylx.ti.R;

import java.util.Objects;

public class GeneralFragment extends PreferenceFragmentCompat implements
        Preference.OnPreferenceChangeListener,
        SharedPreferences.OnSharedPreferenceChangeListener,
        MainActivity.isEnableJavascript {
    private static final String TAG ="SETTING";


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.general, rootKey);
        //SwitchPreference switchPreference=(SwitchPreference) findPreference("notifications");
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(Objects.requireNonNull(getActivity()));
        //boolean turn = sharedPreferences.getBoolean("notifications",true);
        //Log.d(TAG, "onCreatePreferences: "+turn);

    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        return false;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals("notifications")) {
            Log.d(TAG, "Preference value was updated to: " + sharedPreferences.getBoolean(key, true));
        }

    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}

