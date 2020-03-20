package com.example.kiylx.ti.activitys;

import android.Manifest;
import android.os.Bundle;

import com.example.kiylx.ti.conf.PreferenceTools;
import com.example.kiylx.ti.conf.SomeRes;
import com.example.kiylx.ti.conf.WebviewConf;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


import android.view.View;
import android.widget.Toast;

import com.example.kiylx.ti.R;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class StartPageActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_page);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        initConf();
        getAuthority(this);
    }

    /**
     * @param startPageActivity 申请权限
     */

    private void getAuthority(StartPageActivity startPageActivity) {
        String[] perms = {Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.INTERNET};
        if (EasyPermissions.hasPermissions(this,perms)){
        }else {
            EasyPermissions.requestPermissions(this,"这些权限是必须的",20033,perms);
        }
    }

    /**
     * @param requestCode
     * @param permissions
     * @param grantResults
     *
     * 权限请求结果在这里处理
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // 将结果转发到EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    /**
     * 请求权限成功。
     * 可以弹窗显示结果，也可执行具体需要的逻辑操作
     *
     * @param requestCode
     * @param perms
     */
    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        Toast.makeText(getApplicationContext(), "用户授权成功",Toast.LENGTH_LONG).show();
    }
    /**
     * 请求权限失败
     *
     * @param requestCode
     * @param perms
     */
    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        Toast.makeText(getApplicationContext(), "用户授权失败",Toast.LENGTH_LONG).show();
        /**
         * 若是在权限弹窗中，用户勾选了'NEVER ASK AGAIN.'或者'不在提示'，且拒绝权限。
         * 这时候，需要跳转到设置界面去，让用户手动开启。
         */
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        }
    }


    /**
     * 初始化设置文件
     */
    private void initConf() {
        //默认不使用自定义主页
        PreferenceTools.putBoolean(this, WebviewConf.useCustomHomepage, false);
        //主页网址初始化为“”
        PreferenceTools.putString(this, WebviewConf.homepageurl, "");
        {
            //默认useragent，设置时从useragent列表中选择一个写入到这个preference
            PreferenceTools.putString(this, WebviewConf.userAgent, null);

            //内置的useragent列表
            HashMap<String, String> useragentMap = new LinkedHashMap<>();
            useragentMap.put("Chrome", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.106 Safari/537.36");
            useragentMap.put("FireFox", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:72.0) Gecko/20100101 Firefox/72.0");
            useragentMap.put("IE 9.0", "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; Trident/5.0");
            useragentMap.put("iPhone", "Mozilla/5.0 (iPhone; U; CPU iPhone OS 4_3_3 like Mac OS X; en-us) AppleWebKit/533.17.9 (KHTML, like Gecko) Version/5.0.2 Mobile/8J2 Safari/6533.18.5");
            useragentMap.put("默认", null);//默认显示
            PreferenceTools.putHashMap2(this, WebviewConf.userAgentList, useragentMap);
        }

        {
            //字体缩放默认值，选择其他缩放值时把值写入这里，其他地方使用时直接获取这里
            PreferenceTools.putString(this, WebviewConf.textZoom, "100");

            HashMap<String, String> zoomMap = new LinkedHashMap<>();
            zoomMap.put("50 %", "50");
            zoomMap.put("70 %", "70");
            zoomMap.put("90 %", "90");
            zoomMap.put("100 %", "100");//默认显示
            zoomMap.put("110 %", "110");
            zoomMap.put("125 %", "125");
            zoomMap.put("130 %", "130");
            zoomMap.put("150 %", "150");
            PreferenceTools.putHashMap2(this, WebviewConf.textZoomList, zoomMap);
        }

        {
            //默认搜索引擎列表和默认搜索引擎,添加搜索引擎会添加到searchengineList，选择searchengineList中一项时会写入到searchengine这个preference
            PreferenceTools.putString(this, WebviewConf.searchengine, SomeRes.bing);

            HashMap<String, String> searchengineList = new HashMap<>();
            searchengineList.put("百度", SomeRes.baidu);
            searchengineList.put("必应", SomeRes.bing);
            searchengineList.put("搜狗", SomeRes.sougou);
            searchengineList.put("秘迹搜索", SomeRes.miji);
            searchengineList.put("谷歌搜索", SomeRes.google);
            PreferenceTools.putHashMap2(this, WebviewConf.searchengineList, searchengineList);
        }

        //默认不用内置下载器
        PreferenceTools.putBoolean(this, WebviewConf.customDownload, false);
        //是否打开就恢复上次网页，默认是false
        PreferenceTools.putBoolean(this, WebviewConf.resumeData, false);

    }

}
