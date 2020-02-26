package com.example.kiylx.ti.settingFolders;

import android.os.Bundle;

import androidx.lifecycle.ViewModelProviders;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.example.kiylx.ti.R;
import com.example.kiylx.ti.livedata.DefaultValuesManager;
import com.example.kiylx.ti.livedata.DefaultValuesViewModel;

/**
 * 常规的设置
 */
public class ConventionalFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.setting_conventional, rootKey);

        //设置默认值
        final ListPreference userAgent=findPreference("explorer_flags");
        assert userAgent != null;
        //userAgent.setValueIndex(4);

        //更新liveData
        final DefaultValuesViewModel model=ViewModelProviders.of(ConventionalFragment.this).get(DefaultValuesViewModel.class);
        userAgent.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                //更新liveData
                int qw=1;
                if (qw==1){
                    model.getUserAgent().postValue(userAgent.getValue());
                }else{
                    DefaultValuesManager manager=DefaultValuesManager.getInstance();
                    manager.setUserAgent(userAgent.getValue());
                }


                return true;
            }
        });
    }


}
