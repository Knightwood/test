package com.example.kiylx.ti.downloadpack.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.kiylx.ti.R;
import com.example.kiylx.ti.downloadpack.DownloadActivity;
import com.example.kiylx.ti.downloadpack.bean.DownloadInfo;
import com.example.kiylx.ti.model.EventMessage;
import com.example.kiylx.ti.downloadpack.dinterface.DownloadClickMethod;
import com.example.kiylx.ti.tool.LogUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class DownloadingFragment extends RecyclerViewBaseFragment {
    private static DownloadClickMethod controlInterface;
    private static final String TAG = "正在下载fragment";
    private static List<DownloadInfo> downloadInfoList;

    public DownloadingFragment() {
        super();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DownloadActivity activity = (DownloadActivity) requireActivity();
        controlInterface = activity.getInterface();
        mAdapter.setInterface(controlInterface);
    }

    @Override
    public void onStart() {
        super.onStart();
        //注册eventbus，用于downloadManager中数据发生改变时，在这里重新获取数据更新界面
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
        downloadInfoList = null;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceiveMsg(EventMessage message) {
        if (message.getType() == 1) {
            LogUtil.d(TAG, "eventbus接受到了事件，正在更新视图");
            downloadInfoList = getDownloadInfoList();
            updateUI(downloadInfoList);
        }
    }

    @Override
    public List<DownloadInfo> downloadInfoList() {
        if (downloadInfoList == null) {
            downloadInfoList = getDownloadInfoList();
        }
        return downloadInfoList;
    }

    private List<DownloadInfo> getDownloadInfoList(){
        if (controlInterface==null){
            return new ArrayList<>();

        }else {
            return controlInterface.getAllDownload();
        }
    }}

