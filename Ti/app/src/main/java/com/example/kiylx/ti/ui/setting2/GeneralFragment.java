package com.example.kiylx.ti.ui.setting2;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragment;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreference;

import com.example.kiylx.ti.R;
import com.example.kiylx.ti.ui.fragments.CleanDataDialog;

/**
 * 创建者 kiylx
 * 创建时间 2020/4/27 23:14
 */
public class GeneralFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener {
    private static final String TAG = "常规设置fragment";


    public static GeneralFragment newInstance() {
        return new GeneralFragment();
    }
    public GeneralFragment(){
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.setting_general);
        initPreference();
    }

    Preference explorer_flag;
    Preference cleanData;
    Preference textSize;


    private void initPreference() {
        explorer_flag = findPreference("explorer_flags");

        cleanData = findPreference("deleteData");
        cleanData.setOnPreferenceClickListener(this);

        textSize = findPreference("textZoomlist");

    }


    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        Log.d(TAG, "偏好值被改变了: key： " + preference.getKey() + "  值： " + (boolean) newValue);
        return true;
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        switch (preference.getKey()) {
            case "deleteData":
                CleanDataDialog dialog = CleanDataDialog.newInstance();
                FragmentManager manager = getFragmentManager();
                dialog.show(manager, "清除数据");
                break;

        }
        return true;
    }

}
