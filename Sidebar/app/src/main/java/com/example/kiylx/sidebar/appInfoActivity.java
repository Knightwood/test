package com.example.kiylx.sidebar;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;


import java.util.ArrayList;
import java.util.List;

public class appInfoActivity extends AppCompatActivity {
    ListView appInfoListView = null;
    List<AppInfo> appInfos = null;
    AppInfosAdapter infosAdapter = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.appinfo_layout);
        appInfoListView = findViewById(R.id.appinfo_list);
        appInfos = getAppInfos();
        //获取应用信息
        updateUI(appInfos);
        //把数据用适配器更新视图
        Button button_2 = findViewById(R.id.button_1);
        button_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                
            }
        });
         EditText editText= findViewById(R.id.search_1);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence name, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence name, int i, int i1, int i2) {

                infosAdapter.getFilter().filter(name);

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }
        public void updateUI(List<AppInfo> appinfos){
            if(null != appInfos){
                infosAdapter = new AppInfosAdapter(getApplication(), appInfos);
                appInfoListView.setAdapter(infosAdapter);
            }
        }
        public List<AppInfo> getAppInfos(){
            PackageManager pm = getApplication().getPackageManager();
            List<PackageInfo> packageInfos = pm.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES);
            appInfos = new ArrayList<AppInfo>();
            for(PackageInfo packgeInfo : packageInfos){
                String appName = packgeInfo.applicationInfo.loadLabel(pm).toString();
                Drawable drawable = packgeInfo.applicationInfo.loadIcon(pm);
                AppInfo appInfo = new AppInfo(appName,drawable);
                appInfos.add(appInfo);
            }
            return appInfos;

        }



    }
