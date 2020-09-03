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
import com.example.kiylx.ti.downloadpack.DownloadActivity;
import com.example.kiylx.ti.downloadpack.bean.DownloadInfo;
import com.example.kiylx.ti.downloadpack.dinterface.DownloadClickMethod;
import com.example.kiylx.ti.tool.LogUtil;


import java.util.List;

public abstract class RecyclerViewBaseFragment extends Fragment {
    private static final String TAG = "正在下载fragment";
    protected View mRootView;
    protected RecyclerView viewContainer;
    protected BaseAdapter mAdapter;
    protected DownloadClickMethod controlMethod;

    public RecyclerViewBaseFragment() {
        super();
    }

    /**
     * @return 让子类重写此方法，提供不同的fragment的视图。
     */
    @LayoutRes
    protected int fragmentResId() {
        return R.layout.downloadbasefragments;
    }

    /**
     * @return 返回ecyclerview所需要的数据
     */
    public abstract List<DownloadInfo> getDataList();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(fragmentResId(), null);
        viewContainer = mRootView.findViewById(R.id.diListContainer);//recyclerview
        viewContainer.setLayoutManager(new LinearLayoutManager(getActivity()));
        initRecyclerview();
        return mRootView;
    }

    protected void initRecyclerview() {
    }

    public void setControlMethod(DownloadClickMethod controlMethod) {
        this.controlMethod = controlMethod;
        initRecyclerview();
    }
}
