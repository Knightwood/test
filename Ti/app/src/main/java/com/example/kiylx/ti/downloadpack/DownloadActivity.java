package com.example.kiylx.ti.downloadpack;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.NavHostController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;

import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;

import com.example.kiylx.ti.downloadpack.downloadcore.DownloadServices;
import com.example.kiylx.ti.R;
import com.example.kiylx.ti.downloadpack.bean.SimpleDownloadInfo;
import com.example.kiylx.ti.downloadpack.db.DownloadEntity;
import com.example.kiylx.ti.downloadpack.db.DownloadInfoDatabaseUtil;
import com.example.kiylx.ti.downloadpack.db.DownloadInfoViewModel;
import com.example.kiylx.ti.downloadpack.db.InfoTransformToEntitiy;
import com.example.kiylx.ti.downloadpack.fragments.DownloadFinishFragment;
import com.example.kiylx.ti.downloadpack.fragments.DownloadingFragment;
import com.example.kiylx.ti.downloadpack.fragments.RecyclerViewBaseFragment;
import com.example.kiylx.ti.downloadpack.dinterface.DownloadClickMethod;
import com.example.kiylx.ti.tool.LogUtil;
import com.google.android.material.bottomnavigation.BottomNavigationView;


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static androidx.lifecycle.ViewModelProviders.of;

/**
 * 下载管理界面
 */
public class DownloadActivity extends AppCompatActivity {
    private static final String TAG = "下载管理";


    private DownloadServices.DownloadBinder downloadBinder;
    protected DownloadClickMethod controlMethod;
    //与service通信的中间件
    private ServiceConnection connection;

    BottomNavigationView bottomView;//底部导航栏


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);

        connection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                downloadBinder = (DownloadServices.DownloadBinder) service;//向下转型

                boolean b = (downloadBinder == null);
                LogUtil.d(TAG, "binder是不是null：" + b);
                //下载条目xml控制下载所调用的方法
                 controlMethod = downloadBinder.getInferface();
                setControlMethodtoFragment();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                controlMethod = null;
            }
        };
        //绑定服务.下载服务由mainActivity在点击下载窗口中的“开始”的时候开启并绑定到mainActivity，当DownloadActivity被打开始的时候，就只需要绑定下载服务。
        boundDownloadService();

        //工具栏
        Toolbar toolbar = findViewById(R.id.downloadContoltoolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        toolbar.inflateMenu(R.menu.control_download_toolbat_menu);
        toolbar.setOnMenuItemClickListener(item -> {
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
        });

        bottomView = findViewById(R.id.downloadBottomNavigation);

        NavController navController = Navigation.findNavController(DownloadActivity.this, R.id.navs_host_fragment);
        NavigationUI.setupWithNavController(bottomView, navController);


  /*AppBarConfiguration configuration=new AppBarConfiguration.Builder(bottomView.getMenu()).build();
        NavigationUI.setupActionBarWithNavController(this,navController,configuration);*/
    }

    private void setControlMethodtoFragment() {
        Fragment mNavFragment = getSupportFragmentManager().findFragmentById(R.id.navs_host_fragment);
        Fragment fragment = mNavFragment.getChildFragmentManager().getPrimaryNavigationFragment();
        if (fragment instanceof DownloadingFragment) {
            DownloadingFragment fragment1 = (DownloadingFragment) fragment;
            fragment1.setControlMethod(controlMethod);
        }
        if (fragment instanceof DownloadFinishFragment) {
            DownloadFinishFragment fragment2 = (DownloadFinishFragment) fragment;
            fragment2.setControlMethod(controlMethod);

        }
    }

    @Override
    public void onAttachFragment(@NonNull Fragment fragment) {
        LogUtil.d(TAG, "onAttachFragment");
        super.onAttachFragment(fragment);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(connection);
    }

    public DownloadClickMethod getAInterface() {
        LogUtil.d(TAG, "传递给fragment接口");
        controlMethod = this.downloadBinder.getInferface();
        return controlMethod;
    }

    /**
     * 创建菜单
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.control_download_toolbat_menu, menu);
        return true;
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


    List<SimpleDownloadInfo> downloadInfoList = new ArrayList<>();

    /**
     * @return downloadinfo列表
     * 数据库room，使用了livedata，这里观察数据的更新。
     */
    public List<SimpleDownloadInfo> getDownloadList() {
        DownloadInfoViewModel model = of(this).get(DownloadInfoViewModel.class);
        model.getiLiveData().observe(this, downloadEntities -> {
            downloadInfoList.clear();
            for (DownloadEntity entity : downloadEntities) {
                downloadInfoList.add(InfoTransformToEntitiy.transformToSimple(entity));
                LogUtil.d("下载管理", "getDownloadList: " + entity.currentLength / entity.contentLength);
            }
        });

        return null;
    }

    /**
     * 测试方法
     */
    private void getList() {
        new Thread(new Runnable() {
            private List<DownloadEntity> list;

            @Override
            public void run() {
                this.list = DownloadInfoDatabaseUtil.getDao(getApplicationContext()).getAll();
                if (list == null) {
                    LogUtil.d(TAG, "getChildrenList: 数据库出错");
                } else if (list.isEmpty()) {
                    LogUtil.d(TAG, "下载activity：数据库里是空的");
                } else {
                    for (int i = 0; i < list.size(); i++) {
                        LogUtil.d(TAG, "文件名称" + list.get(i).filename
                                + "暂停:" + list.get(i).pause
                                + "等待:" + list.get(i).waitDownload
                                + "完成:" + list.get(i).downloadSuccess
                                + "总大小" + list.get(i).contentLength
                                + "当前大小" + list.get(i).currentLength);
                    }
                }

            }
        }).start();
    }
}
