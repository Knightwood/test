package com.example.kiylx.ti.activitys;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import com.example.kiylx.ti.R;
import com.example.kiylx.ti.conf.SomeRes;
import com.google.android.material.tabs.TabLayout;

public class SettingActivity extends AppCompatActivity {
    private TabLayout tabLayout;
    private FrameLayout fragmentContainer;
    private String[] tabName;
    private Fragment[] fragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        tabName = SomeRes.tabName;
        initView();
    }

    private void initView() {
        tabLayout = f(R.id.setting_tab);
        fragmentContainer = f(R.id.setfragment_container);
        for (int i = 0; i < tabName.length; i++) {
            tabLayout.addTab(new TabLayout.Tab().setText(tabName[i]));
        }

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }


    private <T extends View> T f(int resId) {
        return (T) super.findViewById(resId);
    }
}
