package com.example.kiylx.ti.downloadpack.fragments;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.kiylx.ti.downloadpack.adapter.downloading.DownloadListAdapter;
import com.example.kiylx.ti.downloadpack.core.DownloadInfo;
import com.example.kiylx.ti.downloadpack.viewmodels.DownloadActivityViewModel;
import com.example.kiylx.ti.tool.LogUtil;

import java.util.ArrayList;
import java.util.List;

public class DownloadingFragment extends RecyclerViewBaseFragment {
    private static final String TAG = "Downloadingfragment";
    private DownloadActivityViewModel viewModel;

    public DownloadingFragment() {
        super();
    }

    @Override
    protected void initViewModel() {
        viewModel = new ViewModelProvider(requireActivity()).get(DownloadActivityViewModel.class);
        updateList(viewModel.getDownloadcompleteList().getValue());
        viewModel.getDownloadingList().observe(this, new Observer<List<DownloadInfo>>() {
            @Override
            public void onChanged(List<DownloadInfo> downloadInfos) {
                updateList(downloadInfos);
            }
        });
    }

    @Override
    protected void initRecyclerview(List<DownloadInfo> downloadInfos) {
        super.initRecyclerview(downloadInfos);
        viewContainer.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new DownloadListAdapter(downloadInfos);
        ((DownloadListAdapter) mAdapter).setInterface(control);
        viewContainer.setAdapter(mAdapter);
        LogUtil.d(TAG, "初始化recyclerview");
    }

    @Override
    public void updateList(List<DownloadInfo> list) {
        LogUtil.d(TAG, "正在下载更新recyclerview");
        if (mAdapter == null) {
            LogUtil.d(TAG, "下载完成更新recyclerview中的adapter是null");
            initRecyclerview(list);
            return;
        }
        if (list != null)
            LogUtil.d(TAG, "正在下载更新recyclerview，数据空的？" + list.isEmpty());
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onStop() {
        super.onStop();
    }
}

