package com.example.kiylx.ti.activitys;


import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import android.os.Bundle;

import com.example.kiylx.ti.R;
import com.example.kiylx.ti.settingFolders.MainSettingFragment;

public class SettingActivity2 extends AppCompatActivity implements PreferenceFragmentCompat.OnPreferenceStartFragmentCallback {
private MainSettingFragment mainSettingFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting2);
        if (mainSettingFragment==null){
           mainSettingFragment= new MainSettingFragment();
        }
        getSupportFragmentManager().beginTransaction().add(R.id.ffff, mainSettingFragment).commit();
    }


    @Override
    public boolean onPreferenceStartFragment(PreferenceFragmentCompat caller, Preference pref) {
        // Instantiate the new Fragment
        final Bundle args = pref.getExtras();
        final Fragment fragment = Fragment.instantiate(getApplicationContext(),
                pref.getFragment(),
                args);
        fragment.setArguments(args);
        fragment.setTargetFragment(caller, 0);
        // Replace the existing Fragment with the new Fragment
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.ffff, fragment)
                .addToBackStack(null)
                .commit();
        return true;

    }

}
