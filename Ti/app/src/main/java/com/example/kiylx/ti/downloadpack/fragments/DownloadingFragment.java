package com.example.kiylx.ti.downloadpack.fragments;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.example.kiylx.ti.downloadpack.adapter.downloading.DownloadListAdapter;
import com.example.kiylx.ti.downloadpack.bean.DownloadInfo;
import com.example.kiylx.ti.model.EventMessage;
import com.example.kiylx.ti.tool.LogUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class DownloadingFragment extends RecyclerViewBaseFragment {
    private static final String TAG = "正在下载fragment";
    protected List<DownloadInfo> list;

    public DownloadingFragment() {
        super();
    }

    @Override
    public void onStart() {
        super.onStart();
        //注册eventbus，用于downloadManager中数据发生改变时，在这里重新获取数据更新界面
        EventBus.getDefault().register(this);
    }

    @Override
    protected void initRecyclerview() {
        super.initRecyclerview();
        if (mAdapter == null) {
            mAdapter = new DownloadListAdapter(getDataList());
        } else {
            ((DownloadListAdapter) mAdapter).setData(getDataList());
        }
        ((DownloadListAdapter) mAdapter).setInterface(controlMethod);
        viewContainer.setAdapter(mAdapter);

        LogUtil.d(TAG, "初始化recyclerview");
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
        list = null;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceiveMsg(EventMessage message) {
        if (message.getType() == 1) {
            LogUtil.d(TAG, "eventbus接受到了事件，正在更新视图");
            update();
        }
    }

    /**
     * 更新recyclerview
     */
    private void update() {
        controlMethod.getAllDownload(list);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public List<DownloadInfo> getDataList() {
        if (list == null) {
            list = new ArrayList<>();
        }
        if (controlMethod != null)
            controlMethod.getAllDownload(list);

        for (DownloadInfo info : list) {
            LogUtil.d(TAG, info.getFileName());
        }
        return list;
    }
}

