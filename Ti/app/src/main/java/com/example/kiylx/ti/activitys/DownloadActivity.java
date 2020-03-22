package com.example.kiylx.ti.activitys;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;

import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.kiylx.ti.corebase.DownloadInfo;
import com.example.kiylx.ti.downloadCore.DownloadManager;
import com.example.kiylx.ti.downloadCore.DownloadServices;
import com.example.kiylx.ti.R;
import com.example.kiylx.ti.downloadFragments.DownloadSettingFragment;
import com.example.kiylx.ti.downloadFragments.DownloadFinishFragment;
import com.example.kiylx.ti.downloadFragments.DownloadingFragment;
import com.example.kiylx.ti.downloadInfo_storage.DownloadEntity;
import com.example.kiylx.ti.downloadInfo_storage.DownloadInfoViewModel;
import com.example.kiylx.ti.myInterface.DownloadClickMethod;
import com.google.android.material.bottomnavigation.BottomNavigationView;


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * 下载管理界面
 */
public class DownloadActivity extends AppCompatActivity {


    /**
     * 存放下载的信息的列表
     */
    private List<DownloadInfo> downloadList;
    private DownloadServices.DownloadBinder downloadBinder;
    private DownloadClickMethod controlMethod;
    private int selectPage;//0,1,2表示那三个fragment，在选择底栏三个选项时，会根据它切换，以节省资源。
    private DownloadManager downloadManager = DownloadManager.getInstance();

    public DownloadActivity() {
        super();
        downloadList = new ArrayList<>();

        //downloadList.add(new DownloadInfo("https://mobile-asset.majsoul.com/Downloads/android/majsoul_1_7_2.apk"));
        //downloadList.add(new DownloadInfo("www.baidu.com/ko2"));
        //downloadList.add(new DownloadInfo("www.baidu.com/ko1"));
    }

    //与service通信的中间件
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            downloadBinder = (DownloadServices.DownloadBinder) service;//向下转型
            //下载条目xml控制下载所调用的方法
            controlMethod = downloadBinder.getInferface();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            controlMethod = null;
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);

        //开启下载服务
        //startDownoadService();
        //绑定服务.下载服务由mainActivity在点击下载窗口中的“开始”的时候开启并绑定到mainActivity，当DownloadActivity被打开始的时候，就只需要绑定下载服务。
        boundDownloadService();

        //downloadList=从存储中获取下载信息

        downloadList = downloadManager.getDownloading();
        downloadingFragment();

        //测试开始下载任务的按钮
        Button bui = findViewById(R.id.ceshianniu);
        bui.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //DownloadWindow dof= DownloadWindow.getInstance(new DownloadInfo("www.baidu.com/ko"));
                //FragmentManager fragmentManager=getSupportFragmentManager();
                //dof.show(fragmentManager,"下载");
                boolean as = false;
                downloadBinder.pauseAll();
            }
        });


//工具栏
        Toolbar toolbar = findViewById(R.id.downloadContoltoolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        toolbar.inflateMenu(R.menu.control_download_toolbat_menu);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.allCancel:
                        downloadBinder.cancelAll();
                        break;
                    case R.id.allStart:
                        downloadBinder.resumeAll();
                        break;
                    case R.id.pauseAll:
                        downloadBinder.pauseAll();
                        break;
                }
                return false;
            }
        });
//底栏
        BottomNavigationView bottomView = findViewById(R.id.downloadBottomNavigation);
        bottomView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.downloading:
                        if (selectPage != 0)
                            downloadingFragment();
                        break;
                    case R.id.downloadFinish:
                        if (selectPage != 1)
                            finishFragment();
                        break;
                    case R.id.cancel:
                        if (selectPage != 2)
                            settingFragment();
                        break;
                }
                return false;
            }
        });
    }


    /**
     * 创建菜单
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.control_download_toolbat_menu, menu);
        /*if (downloadBinder.getDownloadingNum()>0){
            //根据有没有下载任务设置不同的图标
            menu.findItem(R.id.allStart).setIcon(R.drawable.ic_pause_black_24dp);
        }else{
            menu.findItem(R.id.allStart).setIcon(R.drawable.ic_play_arrow_black_24dp);
        }*/

        return true;
    }


    /*
   系统调用onCreateOptionsMenu方法后，将保留创建的Menu实例。
除非菜单由于某些原因而失效，否则不会再次调用onCreateOptionsMenu。
因此，我们只应该使用onCreateOptionsMenu来创建初始菜单状态，而不应使用它在Activity生命周期中对菜单执行任何更改。
   如果需要根据在Activity生命周期中发生的某些事件修改选项菜单，则应该通过onPrepareOptionsMenu方法实现。
这个方法的参数中有一个Menu对象（即旧的Menu对象），我们可以使用它对菜单执行修改，如添加、移除、启用或禁用菜单项。
（Fragment同样提供onPrepareOptionsMenu方法，只是不需要提供返回值）
   需要注意，在Android 3.0及更高版本中，当菜单项显示在应用栏中时，选项菜单被视为始终处于打开状态。
发生事件时，如果要执行菜单更新，则必须调用 invalidateOptionsMenu来请求系统调用onPrepareOptionsMenu方法。
*/

    /**
     * @param menu
     * @return 改变toolbar中按钮的状态
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    /**
     * 开启下载服务，并绑定服务（混合绑定），以此保证服务在后台运行：
     * 即使downloadActivivty结束也可以继续在后台运行
     */
    private void startDownoadService() {
        Intent intent = new Intent(DownloadActivity.this, DownloadServices.class);
        startService(intent);
        bindService(intent, connection, BIND_AUTO_CREATE);
    }

    /**
     * 绑定下载服务，以此控制下载服务中的下载。
     */
    private void boundDownloadService() {
        Intent intent = new Intent(DownloadActivity.this, DownloadServices.class);
        bindService(intent, connection, BIND_AUTO_CREATE);
    }

    /**
     * 开启正在下载fragment
     */
    public void downloadingFragment() {
        selectPage = 0;
        DownloadingFragment fragment = DownloadingFragment.getInstance(controlMethod, downloadList);
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.downloadfragmentcontainer, fragment).commit();

    }

    /**
     * 开启完成下载的fragment
     */
    public void finishFragment() {
        selectPage = 1;
        DownloadFinishFragment fragment = new DownloadFinishFragment(null);
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.downloadfragmentcontainer, fragment).commit();
    }

    /**
     * 开启下载设置界面
     */
    public void settingFragment() {
        selectPage = 2;
        DownloadSettingFragment fragment = new DownloadSettingFragment(null);
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.downloadfragmentcontainer, fragment).commit();
    }

    List<DownloadInfo> downloadInfoList = new ArrayList<>();

    /**
     * @return downloadinfo列表
     * 数据库room，使用了livedata，这里观察数据的更新。
     */
    public List<DownloadInfo> getDownloadList() {
        DownloadInfoViewModel model = ViewModelProviders.of(this).get(DownloadInfoViewModel.class);
        model.getiLiveData().observe(this, new Observer<List<DownloadEntity>>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onChanged(List<DownloadEntity> downloadEntities) {
                downloadInfoList.clear();
                Stream<DownloadEntity> stream=downloadEntities.stream();
            }
        });

        return null;
    }

}
