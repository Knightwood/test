package com.example.kiylx.ti.trash;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.widget.FrameLayout;

import com.example.kiylx.ti.R;
import com.example.kiylx.ti.ui.settings.AboutFragment;
import com.example.kiylx.ti.ui.settings.PrivacyFragment;
import com.google.android.material.tabs.TabLayout;

public class SettingActivity extends AppCompatActivity {
    private TabLayout tabLayout;
    private FrameLayout fragmentContainer;
    private FragmentManager fragmentManager;
    private static int lastSelect = 0;
    private Fragment currentFragment;
    private String[] fragmentsTag = new String[]{"general", "privacy", "stylel", "about"};

    //   设置页面的tab标题："常规",   "隐私",    "样式",  "关于"
    // fragment 的tag ："general","privacy","stylel","about"


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
       currentFragment=GeneralFragment.newInstance();
        fragmentManager.beginTransaction().add(R.id.setfragment_container,currentFragment ,"general").commit();

    }

    private void initView() {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        if (lastSelect != 0)
                            switchFragment(0,fragmentsTag[0]);
                        break;
                    case 1:
                        if (lastSelect != 1)
                            switchFragment(1,fragmentsTag[1]);
                        break;
                    case 2:
                        if (lastSelect != 2)
                            switchFragment(2,fragmentsTag[2]);
                        break;
                    case 3:
                        if (lastSelect != 3)
                            switchFragment(3,fragmentsTag[3]);
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

    private void switchFragment(int i,String tag) {
        lastSelect = i;
        FragmentManager manager = getSupportFragmentManager();
        if (currentFragment!=null){
            manager.beginTransaction().hide(currentFragment).commit();
        }
        currentFragment=manager.findFragmentByTag(tag);
        if (currentFragment==null){
            switch (tag) {
                case "general":
                    currentFragment = GeneralFragment.newInstance();
                    break;
                case "privacy":
                    currentFragment = PrivacyFragment.newInstance();
                    break;
                case "stylel":
                    currentFragment = ThemeFragment.newInstance();
                    break;
                case "about":
                    currentFragment = AboutFragment.newInstance();
                    break;
            }

            manager.beginTransaction().add(R.id.setfragment_container,currentFragment,tag).commit();

        }else{
            manager.beginTransaction().show(currentFragment).commit();
        }
    }
}
/*private void switchFragment(int i) {
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

    }*/