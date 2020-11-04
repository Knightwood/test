package com.example.kiylx.ti.downloadpack.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.kiylx.ti.R;
import com.example.kiylx.ti.downloadpack.adapter.complete.DownloadCompleteAdapter;
import com.example.kiylx.ti.downloadpack.core.DownloadInfo;
import com.example.kiylx.ti.downloadpack.viewmodels.DownloadActivityViewModel;
import com.example.kiylx.ti.tool.LogUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DownloadFinishFragment extends RecyclerViewBaseFragment {
    private static final String TAG = "DownlaodFinishfragment";
    private DownloadActivityViewModel viewModel;

    public DownloadFinishFragment() {
        super();
    }


    @Override
    protected void initViewModel() {
        viewModel = new ViewModelProvider(requireActivity()).get(DownloadActivityViewModel.class);
        updateList(viewModel.getDownloadcompleteList().getValue());
        viewModel.getDownloadcompleteList().observe(this, new Observer<List<DownloadInfo>>() {
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
        mAdapter = new DownloadCompleteAdapter(downloadInfos);
        ((DownloadCompleteAdapter) mAdapter).setClickListener(this::popMenu);
        ((DownloadCompleteAdapter) mAdapter).setInterface(control);
        viewContainer.setAdapter(mAdapter);
        if (downloadInfos != null) {
            LogUtil.d(TAG, "初始化recyclerview   数据空的？" + downloadInfos.isEmpty());
        } else{
            LogUtil.d(TAG, "初始化recyclerview   数据是null");
        }
    }

    @Override
    public void updateList(List<DownloadInfo> list) {
        if (mAdapter == null) {
            LogUtil.d(TAG, "下载完成更新recyclerview中的adapter是null");
            initRecyclerview(list);
            return;
        }
        if (list != null)
        LogUtil.d(TAG, "下载完成更新recyclerview，数据空的？" + list.isEmpty());
        mAdapter.notifyDataSetChanged();
    }


    public void popMenu(View v, DownloadInfo info) {
        PopupMenu menu = new PopupMenu(getContext(), v);
        MenuInflater inflater = menu.getMenuInflater();
        inflater.inflate(R.menu.open_download_file, menu.getMenu());
        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.openFile:
                        break;
                    case R.id.deleteFile:
                        break;
                }
                return true;
            }
        });
        menu.show();

    }

    public Intent getFileOpenIntent(String path, String Suffix) {
        File file = new File(path);
        if (file.exists()) {
            Uri uri = Uri.fromFile(file);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, Suffix);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } else {
            Toast.makeText(getContext(), "文件不存在或已删除", Toast.LENGTH_LONG).show();
        }
        return null;
    }
}
