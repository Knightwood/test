package com.example.kiylx.sidebar;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.SearchView;

import java.util.ArrayList;
import java.util.List;

public class appInfoActivity extends AppCompatActivity {
    ListView appInfoListView = null;
    List<AppInfo> appInfos = null;
    AppInfosAdapter infosAdapter = null;
    SearchView searchview;
    nameAdapter name_1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.appinfo_layout);
        appInfoListView = findViewById(R.id.appinfo_list);
        appInfos = getAppInfos();
        //获取应用信息
        updateUI(appInfos);
        //把数据用适配器更新视图
        searchview = findViewById(R.id.search_1);
        searchview.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                updateUI_1(s);
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
        public void updateUI_1(String name){
        if(name != null){
            name_1 = new nameAdapter(appInfos, name );
            appInfoListView.setAdapter(name_1);
        }

        }


    }
