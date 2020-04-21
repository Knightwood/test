package com.example.kiylx.ti.ui.activitys;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.widget.FrameLayout;

import com.example.kiylx.ti.R;
import com.example.kiylx.ti.ui.settings.AboutFragment;
import com.example.kiylx.ti.ui.settings.GeneralFragment;
import com.example.kiylx.ti.ui.settings.PrivacyFragment;
import com.example.kiylx.ti.ui.settings.ThemeFragment;
import com.google.android.material.tabs.TabLayout;

public class SettingActivity extends AppCompatActivity {
    private TabLayout tabLayout;
    private FrameLayout fragmentContainer;
    //private List<Fragment> fragments;
    private FragmentManager fragmentManager;
    private static int lastSelect = 0;

    //设置页面的tab标题："常规", "隐私", "样式", "关于"


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        tabLayout = findViewById(R.id.setting_tab);
        fragmentContainer = findViewById(R.id.setfragment_container);
        fragmentManager = getSupportFragmentManager();

        initFragment();
        initView();

    }

    private void initFragment() {
        /*if (fragments == null || fragments.isEmpty()) ;
        {
            fragments = new ArrayList<>();
            fragments.add(GeneralFragment.newInstance());
            fragments.add(PrivacyFragment.newInstance());
            fragments.add(ThemeFragment.newInstance());
            fragments.add(AboutFragment.newInstance());
        }*/
        fragmentManager.beginTransaction().add(R.id.setfragment_container, GeneralFragment.newInstance()).commit();

    }

    private void initView() {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        if (lastSelect != 0)
                            switchFragment(0);
                        break;
                    case 1:
                        if (lastSelect != 1)
                            switchFragment(1);
                        break;
                    case 2:
                        if (lastSelect != 2)
                            switchFragment(2);
                        break;
                    case 3:
                        if (lastSelect != 3)
                            switchFragment(3);
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    private void switchFragment(int i) {
        lastSelect = i;
        FragmentManager manager = getSupportFragmentManager();
        Fragment fragment;
        switch (i) {
            case 0:
                fragment = GeneralFragment.newInstance();
                manager.beginTransaction().replace(R.id.setfragment_container, fragment).commit();
                break;
            case 1:
                fragment = PrivacyFragment.newInstance();
                manager.beginTransaction().replace(R.id.setfragment_container, fragment).commit();
                break;
            case 2:
                fragment = ThemeFragment.newInstance();
                manager.beginTransaction().replace(R.id.setfragment_container, fragment).commit();
                break;
            case 3:
                fragment = AboutFragment.newInstance();
                manager.beginTransaction().replace(R.id.setfragment_container, fragment).commit();
                break;
        }

    }
}
