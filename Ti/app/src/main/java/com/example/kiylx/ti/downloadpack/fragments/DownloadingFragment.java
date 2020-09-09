package com.example.kiylx.ti.downloadpack.fragments;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.kiylx.ti.downloadpack.adapter.downloading.DownloadListAdapter;
import com.example.kiylx.ti.downloadpack.core.DownloadInfo;
import com.example.kiylx.ti.downloadpack.viewmodels.DownloadActivityViewModel;
import com.example.kiylx.ti.tool.LogUtil;

import java.util.ArrayList;
import java.util.List;

public class DownloadingFragment extends RecyclerViewBaseFragment {
    private static final String TAG = "Downloadingfragment";
    protected List<DownloadInfo> infos = null;
    private DownloadActivityViewModel viewModel;

    public DownloadingFragment() {
        super();
    }

    @Override
    protected void initViewModel(){
       viewModel=new ViewModelProvider(requireActivity()).get(DownloadActivityViewModel.class);

       viewModel.getDownloadingList().observe(this, new Observer<List<DownloadInfo>>() {
           @Override
           public void onChanged(List<DownloadInfo> downloadInfos) {
               if (infos==null){
                   infos  =downloadInfos;
                   initRecyclerview();
               }else{
                   infos.clear();
                   infos.addAll(downloadInfos);
               }
               updateList(downloadInfos);
           }
       });
    }

    @Override
    protected void initRecyclerview() {
        super.initRecyclerview();
        if (mAdapter == null)
            mAdapter = new DownloadListAdapter(infos);
        else
            mAdapter.setData(infos);
        ((DownloadListAdapter) mAdapter).setInterface(control);
        viewContainer.setAdapter(mAdapter);
        LogUtil.d(TAG, "初始化recyclerview");
    }

    @Override
    public void updateList(List<DownloadInfo> list) {
        LogUtil.d(TAG, "正在下载更新recyclerview，数据空的？"+list.isEmpty());
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onStop() {
        super.onStop();
    }
}

