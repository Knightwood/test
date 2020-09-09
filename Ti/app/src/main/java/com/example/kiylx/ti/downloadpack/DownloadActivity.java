package com.example.kiylx.ti.downloadpack;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;

import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;

import com.example.kiylx.ti.downloadpack.core.DownloadInfo;
import com.example.kiylx.ti.downloadpack.dinterface.ItemControl;
import com.example.kiylx.ti.downloadpack.dinterface.SendData;
import com.example.kiylx.ti.downloadpack.downloadcore.DownloadServices;
import com.example.kiylx.ti.R;
import com.example.kiylx.ti.downloadpack.db.DownloadEntity;
import com.example.kiylx.ti.downloadpack.db.DownloadInfoDatabaseUtil;
import com.example.kiylx.ti.downloadpack.fragments.DownloadFinishFragment;
import com.example.kiylx.ti.downloadpack.fragments.DownloadingFragment;
import com.example.kiylx.ti.downloadpack.dinterface.ControlDownloadMethod;
import com.example.kiylx.ti.downloadpack.viewmodels.DownloadActivityViewModel;
import com.example.kiylx.ti.model.EventMessage;
import com.example.kiylx.ti.tool.LogUtil;
import com.google.android.material.bottomnavigation.BottomNavigationView;


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;
import java.util.Objects;

import static androidx.lifecycle.ViewModelProviders.of;

/**
 * 下载管理界面
 */
public class DownloadActivity extends AppCompatActivity implements ItemControl, SendData {
    private static final String TAG = "DownloadActivity";
    private DownloadActivityViewModel viewModel;
    private DownloadServices.DownloadBinder downloadBinder;
    protected ControlDownloadMethod controlMethod;
    //与service通信的中间件
    private ServiceConnection connection;
    BottomNavigationView bottomView;//底部导航栏


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        //viewmodel
        viewModel = new ViewModelProvider(this).get(DownloadActivityViewModel.class);
        connection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                downloadBinder = (DownloadServices.DownloadBinder) service;//向下转型
                //下载条目xml控制下载所调用的方法
                controlMethod = downloadBinder.getInferface();
                downloadBinder.newDownloadManager(DownloadActivity.this);
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

        //导航
        NavController navController = Navigation.findNavController(DownloadActivity.this, R.id.navs_host_fragment);
        NavigationUI.setupWithNavController(bottomView, navController);
    }

    @Override
    protected void onStart() {
        super.onStart();
        //注册eventbus，用于downloadManager中数据发生改变时，在这里重新获取数据更新界面
        EventBus.getDefault().register(this);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceiveMsg(EventMessage message) {
        if (message.getType() == 1) {
            LogUtil.d(TAG, "eventbus接受到了事件，正在更新视图");
            viewModel.setDownloadingList(controlMethod.getAllDownload());
            viewModel.setDownloadcompleteList(controlMethod.getAllComplete());
        }
    }

    /**
     * 将itemcontrol接口实现传递给fragment
     */
    private void setControlMethodtoFragment() {
        Fragment mNavFragment = getSupportFragmentManager().findFragmentById(R.id.navs_host_fragment);
        Fragment fragment = mNavFragment.getChildFragmentManager().getPrimaryNavigationFragment();
        if (fragment instanceof DownloadingFragment) {
            DownloadingFragment fragment1 = (DownloadingFragment) fragment;
            fragment1.setControl(this);
        }
        if (fragment instanceof DownloadFinishFragment) {
            DownloadFinishFragment fragment2 = (DownloadFinishFragment) fragment;
            fragment2.setControl(this);

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(connection);
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

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
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

    @Override
    public void download(DownloadInfo info) throws Exception {
        controlMethod.download(info);
    }

    @Override
    public void pause(DownloadInfo info) {
        controlMethod.pause(info);
    }

    @Override
    public void cancel(DownloadInfo info) {
        controlMethod.cancel(info);
    }

    @Override
    public void resume(DownloadInfo info) {
        controlMethod.reasume(info);
    }

    @Override
    public float getPercent(DownloadInfo info) {
        return 0;
    }

    @Override
    public void allDownloading(List<DownloadInfo> downloading) {
        viewModel.setDownloadingList(downloading);

    }

    @Override
    public void readyDownload(List<DownloadInfo> ready) {

    }

    @Override
    public void downloading(List<DownloadInfo> downloading) {

    }

    @Override
    public void pausedownload(List<DownloadInfo> paused) {

    }

    @Override
    public void completeDownload(List<DownloadInfo> complete) {
        viewModel.setDownloadcompleteList(complete);
    }

    @Override
    public void notifyUpdate() {
        viewModel.setDownloadingList(controlMethod.getAllDownload());
        viewModel.setDownloadcompleteList(controlMethod.getAllComplete());

    }
}
