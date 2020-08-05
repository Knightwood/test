package com.example.kiylx.ti.ui.setting2;

import android.content.Intent;
import android.os.Bundle;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.example.kiylx.ti.R;
import com.example.kiylx.ti.tool.LogUtil;
import com.example.kiylx.ti.ui.activitys.ExtentActivity;

public class BetaFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceClickListener {
    private static final String TAG = "实验室preference";

    public static BetaFragment newInstance() {
        return new BetaFragment();
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.setting_beta, rootKey);
        Preference jsManagerPreference=findPreference("js_manager");
        jsManagerPreference.setOnPreferenceClickListener(this);
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        switch (preference.getKey()) {
            case "js_manager":
                Intent intent = new Intent(getContext(), ExtentActivity.class);
                startActivity(intent);
                break;
        }

        return true;
    }
}
