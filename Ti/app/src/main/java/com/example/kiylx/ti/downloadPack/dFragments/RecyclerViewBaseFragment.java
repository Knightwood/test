package com.example.kiylx.ti.downloadPack.dFragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kiylx.ti.R;
import com.example.kiylx.ti.downloadPack.base.DownloadInfo;


import java.util.List;

public abstract class RecyclerViewBaseFragment extends Fragment {
    private static final String TAG = "正在下载fragment";
    private View mRootView;
    private RecyclerView viewContainer;
    private List<DownloadInfo> mDownloadInfoArrayList;
    private listAdapter mAdapter;

    /**
     * @return 让子类重写此方法，提供不同的fragment的视图。
     */
    @LayoutRes
    public int getresId() {
        return R.layout.downloadbasefragments;
    }

    /**
     * @return 返回item的视图
     */
    @LayoutRes
    public int getItemResId() {
        return R.layout.download_item;
    }

    public abstract List<DownloadInfo> downloadInfoList();


    /**
     * @param v    条目视图
     * @param info 绑定到视图的数据
     *             交给子类实现去绑定视图以及控制方法
     */
    public abstract void bindItemView(View v, DownloadInfo info);

    public RecyclerViewBaseFragment() {
        super();
        this.mDownloadInfoArrayList=downloadInfoList();
        Log.d(TAG, "无参构造");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        mRootView = inflater.inflate(getresId(), null);
        viewContainer = mRootView.findViewById(R.id.diListContainer);//recyclerview
        viewContainer.setLayoutManager(new LinearLayoutManager(getContext()));
        updateUI();
        return mRootView;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void updateUI() {
        Log.d(TAG, "updateUI: ");
        if (mDownloadInfoArrayList == null) {
            Log.d(TAG, "下载信息列表不存在 ");
            return;
        }
        if (mAdapter == null) {
            mAdapter = new listAdapter(mDownloadInfoArrayList);
            viewContainer.setAdapter(mAdapter);
            Log.d(TAG, "适配器创建");
        } else {
            mAdapter.setLists(mDownloadInfoArrayList);
            mAdapter.notifyDataSetChanged();
            Log.d(TAG, "更新数据");
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

    private class listAdapter extends RecyclerView.Adapter<DownloadViewHolder> {
        private List<DownloadInfo> mLists;//recyclerview所用的数据

        public listAdapter(List<DownloadInfo> list) {
            this.mLists = list;
        }

        public void setLists(List<DownloadInfo> list) {
            this.mLists = list;
        }

        @NonNull
        @Override
        public DownloadViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(getItemResId(), parent, false);
            //DownloadItemBinding itemBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.download_item, parent, false);
            return new DownloadViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull DownloadViewHolder holder, int position) {
            holder.bindView(mLists.get(position));
        }

        @Override
        public int getItemCount() {
            return mLists.size();
        }
    }

    public class DownloadViewHolder extends RecyclerView.ViewHolder {
        private DownloadInfo mDownloadInfo;
        private View downloadItemView;

        public DownloadViewHolder(@NonNull View itemView) {
            super(itemView);
            downloadItemView = itemView;
        }

        public void bindView(DownloadInfo info) {
            this.mDownloadInfo = info;
            //绑定视图，这里由子类实现
            bindItemView(downloadItemView, info);
        }
    }
    /*public class DownloadViewHolder extends RecyclerView.ViewHolder {
        DownloadInfo mDownloadInfo;
        private DownloadItemBinding mBinding;

        public DownloadViewHolder(@NonNull DownloadItemBinding itemBinding) {
            super(itemBinding.getRoot());
            this.mBinding=itemBinding;

            //绑定上viewModel
            //mClickMethod是item布局控制实际下载方法的途径，由fragment依附的DownloadActivity实现
            mBinding.setItemcontrol(new DownloadControlViewModel(mClickMethod));
        }


        public void bindView(DownloadInfo info) {
            this.mDownloadInfo = info;
            //设置数据
            mBinding.getItemcontrol().setDownloadInfo(mDownloadInfo);
            bindItemView(mRootView,info);
        }

    }*/

}
