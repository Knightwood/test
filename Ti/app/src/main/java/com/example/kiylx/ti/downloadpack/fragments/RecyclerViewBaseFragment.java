package com.example.kiylx.ti.downloadpack.fragments;

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
import com.example.kiylx.ti.downloadpack.core.DownloadInfo;
import com.example.kiylx.ti.downloadpack.dinterface.ItemControl;


import java.util.List;

public abstract class RecyclerViewBaseFragment extends Fragment {
    private static final String TAG = "BaseDownloadFragment";
    protected View mRootView;
    protected RecyclerView viewContainer;
    protected BaseAdapter mAdapter;
    protected ItemControl control;

    public RecyclerViewBaseFragment() {
        super();
    }
    protected void initViewModel(){}

    /**
     * @return 让子类重写此方法，提供不同的fragment的视图。
     */
    @LayoutRes
    protected int fragmentResId() {
        return R.layout.downloadbasefragments;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void setControl(ItemControl control) {
        this.control = control;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(fragmentResId(), null);
        viewContainer = mRootView.findViewById(R.id.diListContainer);//recyclerview
        initViewModel();
        return mRootView;
    }

    protected void initRecyclerview(List<DownloadInfo> downloadInfos) {
    }


    public abstract void updateList(List<DownloadInfo> list);

    @Override
    public void onStop() {
        super.onStop();
    }
}
