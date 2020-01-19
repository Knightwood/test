package com.example.kiylx.ti.downloadFragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kiylx.ti.R;
import com.example.kiylx.ti.corebase.DownloadInfo;
import com.example.kiylx.ti.databinding.DownloadItemBinding;
import com.example.kiylx.ti.model.DownloadControlViewModel;
import com.example.kiylx.ti.myInterface.DownloadClickMethod;

import java.util.ArrayList;

public class DownloadbaseFragment extends Fragment {

    private View mRootView;
    RecyclerView viewContainer;
    private ArrayList<DownloadInfo> mDownloadInfoArrayList;
    private listAdapter mAdapter;

    @LayoutRes
    private int getresId() {
        return R.layout.downloadbasefragments;
    }

    public DownloadbaseFragment() {
        super();
    }

    public void setDownloadInfoArrayList(ArrayList<DownloadInfo> list) {
        this.mDownloadInfoArrayList = list;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        mRootView = inflater.inflate(getresId(), null);
        viewContainer = mRootView.findViewById(R.id.diListContainer);//recyclerview
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

    private void updateUI() {
        viewContainer.setLayoutManager(new LinearLayoutManager(getContext()));
        if (mAdapter == null) {
            mAdapter = new listAdapter();
            mAdapter.setLists(mDownloadInfoArrayList);
        } else {
            mAdapter.notifyDataSetChanged();
        }
        viewContainer.setAdapter(mAdapter);

    }

    private class listAdapter extends RecyclerView.Adapter<DownloadViewHolder> {
        private ArrayList<DownloadInfo> mLists;//recyclerview所用的数据


        void setLists(ArrayList<DownloadInfo> list) {
            this.mLists = list;
        }

        @NonNull
        @Override
        public DownloadViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            //View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.download_item, parent, false);
            DownloadItemBinding itemBinding= DataBindingUtil.inflate(getLayoutInflater(),R.layout.download_item,parent, false);
            return new DownloadViewHolder(itemBinding);
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
        DownloadInfo mDownloadInfo;
        private DownloadItemBinding mBinding;

        public DownloadViewHolder(@NonNull DownloadItemBinding itemBinding) {
            super(itemBinding.getRoot());
            this.mBinding=itemBinding;

            //绑定上viewModel
            mBinding.setControl(new DownloadControlViewModel(new DownloadClickMethod() {
                @Override
                public void download() {

                }

                @Override
                public void pause() {

                }

                @Override
                public void cancel() {

                }
            }));
        }


        public void bindView(DownloadInfo info) {
            this.mDownloadInfo = info;
            bind1(mBinding.getRoot(),mDownloadInfo);
        }

    }

    /**
     * @param v 条目视图
     * @param info 绑定到视图的数据
     */
    public void bind1(View v,DownloadInfo info){

    }

}
