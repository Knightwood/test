package com.example.kiylx.ti.ui.setting2;

import android.os.Bundle;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.example.kiylx.ti.R;

/**
 * 创建者 kiylx
 * 创建时间 2020/4/27 22:43
 */
public class Setting2Activity extends AppCompatActivity implements PreferenceFragmentCompat.OnPreferenceStartFragmentCallback {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting2);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,GeneralFragment.newInstance());
    }


    @Override
    public boolean onPreferenceStartFragment(PreferenceFragmentCompat caller, Preference pref) {

        final Bundle args=pref.getExtras();
        final Fragment fragment=getSupportFragmentManager().getFragmentFactory().instantiate(getClassLoader(),pref.getFragment());
        fragment.setArguments(args);
        fragment.setTargetFragment(caller,0);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,fragment)
                .addToBackStack(null)
                .commit();

        return true;
    }
}
