package com.example.kiylx.ti.downloadpack.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.crystal.customview.baseadapter1.BaseAdapter;
import com.example.kiylx.ti.R;
import com.example.kiylx.ti.downloadpack.adapter.downloading.DownloadHolder;
import com.example.kiylx.ti.downloadpack.adapter.downloading.DownloadListAdapter;
import com.example.kiylx.ti.downloadpack.bean.DownloadInfo;
import com.example.kiylx.ti.tool.LogUtil;


import java.util.List;

public abstract class RecyclerViewBaseFragment extends Fragment {
    private static final String TAG = "正在下载fragment";
    private View mRootView;
    private RecyclerView viewContainer;
    private List<DownloadInfo> mDownloadInfoArrayList;
    protected DownloadListAdapter mAdapter;


    public RecyclerViewBaseFragment() {
        super();
        this.mDownloadInfoArrayList = downloadInfoList();
        LogUtil.d(TAG, "无参构造");
    }

    /**
     * @return 让子类重写此方法，提供不同的fragment的视图。
     */
    @LayoutRes
    protected int fragmentResId() {
        return R.layout.downloadbasefragments;
    }

    public abstract List<DownloadInfo> downloadInfoList();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        mRootView = inflater.inflate(fragmentResId(), null);
        viewContainer = mRootView.findViewById(R.id.diListContainer);//recyclerview
        viewContainer.setLayoutManager(new LinearLayoutManager(getContext()));
        updateUI();
        return mRootView;
    }


    public void updateUI() {
        LogUtil.d(TAG, "updateUI: ");
        if (mDownloadInfoArrayList == null) {
            LogUtil.d(TAG, "下载信息列表不存在 ");
            return;
        }
        if (mAdapter == null) {
            mAdapter = new DownloadListAdapter(mDownloadInfoArrayList);
            viewContainer.setAdapter(mAdapter);
            LogUtil.d(TAG, "适配器创建");
        } else {
            mAdapter.setData(mDownloadInfoArrayList);
            mAdapter.notifyDataSetChanged();
            LogUtil.d(TAG, "更新数据");
        }

    }

    /**
     * @param list 存储下载信息的列表
     *             传入新的数据，更新界面
     */
    public void updateUI(List<DownloadInfo> list) {
        this.mDownloadInfoArrayList.clear();
        this.mDownloadInfoArrayList = list;
        updateUI();
    }

}
