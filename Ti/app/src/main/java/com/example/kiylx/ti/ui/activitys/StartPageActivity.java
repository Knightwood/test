package com.example.kiylx.ti.ui.activitys;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;

import com.example.kiylx.ti.tool.PreferenceTools;
import com.example.kiylx.ti.conf.SomeRes;
import com.example.kiylx.ti.conf.WebviewConf;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;


import android.os.Environment;
import android.view.View;
import android.widget.Toast;

import com.example.kiylx.ti.R;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class StartPageActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    //权限
    String[] allperm = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.INTERNET};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_page);
        //Toolbar toolbar = findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                Intent intent = new Intent(StartPageActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        initConf();
        getAuthority(this);
    }

    /**
     * @param startPageActivity 申请权限
     */

    private void getAuthority(StartPageActivity startPageActivity) {

        if (EasyPermissions.hasPermissions(this, allperm)) {
            //测试权限申请时可以注释掉下面这句
            writeInstalled(true);
            Toast.makeText(this, "有权限了", Toast.LENGTH_LONG).show();
        } else {
            EasyPermissions.requestPermissions(this, "这些权限是必须的", 20033, allperm);
        }
    }

    /**
     * @param b 布尔值
     *          <p>
     *          控制是不是第一次打开，安装应用后第一次打开，“Installed”preference是不存在的，也就是false。
     *          这时如果写入true，则意味着应用已安装，权限之类的也已经配置好了，可以打开mainactivity了。
     *          之后再打开应用，这个值是true，就不会再打开“启动页”配置权限，初始化配置信息之类的了。
     *          如果应用升级了，还可以使用它引导到启动页
     */
    private void writeInstalled(boolean b) {
        PreferenceTools.putBoolean(this, "Installed", b);
    }

    /**
     * @param requestCode  请求码
     * @param permissions  权限集合
     * @param grantResults 请求结果
     *                     <p>
     *                     onRequestPermissionsResult是Android的方法，用来处理权限请求后的事情
     *                     <p>
     *                     EasyPermissions.onRequestPermissionsResult，在这里被转发到easyPermission处理
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // 将结果转发到EasyPermissions进行处理
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
        //测试权限申请时可以注释掉下面这句
        writeInstalled(true);
        Toast.makeText(getApplicationContext(), "用户授权成功", Toast.LENGTH_LONG).show();
    }

    /**
     * easypermission的接口，请求权限失败后被调用
     *
     * @param requestCode
     * @param perms
     */
    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        Toast.makeText(getApplicationContext(), "用户授权失败", Toast.LENGTH_LONG).show();
        //(可选的)这里检查用户是否拒绝授权权限，以及点击了“不再询问”，这时，将展示一个对话框指导用户在应用设置里授权权限
        //如果没有点击不再询问，这里是不会被调用的，如果点击了不再询问，则展示对话框提示设置权限
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this)
                    .setRationale("没有该权限，此应用程序可能无法正常工作。打开应用设置屏幕以修改应用权限")
                    .setTitle("必需权限")
                    .build()
                    .show();
        } else {
            //没有点击不再询问，则再一次申请权限
            EasyPermissions.requestPermissions(this, "这些权限是必须的", 20033, allperm);
        }
    }
    /*
     * EasyPermissions.somePermissionPermanentlyDenied(this, perms):
     *
     * 检查被拒绝的权限列表中是否至少有一个权限被永久拒绝(用户单击“不要再问”)。
     * 注意:由于Android框架权限API提供的信息的限制，
     * 此方法仅在权限被拒绝且您的应用程序已收到onPermissionsDenied回调后才有效。
     * 否则，该库无法将永久拒绝与“尚未拒绝”情况区分开来
     *
     * */

    /**
     * 初始化设置文件
     */
    private void initConf() {
        //默认不使用自定义主页
        PreferenceTools.putBoolean(this, WebviewConf.useCustomHomepage, false);
        //主页网址初始化为“”
        PreferenceTools.putString(this, WebviewConf.homepageurl, SomeRes.default_homePage_url);
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
        {
            //默认不用内置下载器
            PreferenceTools.putBoolean(this, WebviewConf.customDownload, false);
            //是否打开就恢复上次网页，默认是false
            PreferenceTools.putBoolean(this, WebviewConf.resumeData, false);
            //默认下载路径
            PreferenceTools.putString(this, WebviewConf.defaultDownloadPath, Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath());
            //默认最大下载任务数
            PreferenceTools.putInt(this, WebviewConf.defaultDownloadlimit, 3);
            //默认下载线程数
            PreferenceTools.putInt(this, WebviewConf.defaultDownloadthread, 8);
        }

        {
            //使用新的样式的搜索页面
            PreferenceTools.putBoolean(this, SomeRes.SearchViewStyle, true);
            //是否使用搜索匹配
            PreferenceTools.putBoolean(this, SomeRes.searchMatcher, false);
            //是否使用默认模式（不使用fileChooserParams.createIntent();而是自己指定intent的type是“*/*”,以匹配所有类型文件）上传文件,默认不使用新模式
            PreferenceTools.putBoolean(this, WebviewConf.uploadMode, true);
        }
    }

}
