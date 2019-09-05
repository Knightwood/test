package com.example.kiylx.ti;


import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.example.kiylx.ti.settingFolders.GeneralFragment;

public class SettingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        getSupportFragmentManager().beginTransaction().replace(R.id.ffff,new GeneralFragment()).commit();
    }

}
