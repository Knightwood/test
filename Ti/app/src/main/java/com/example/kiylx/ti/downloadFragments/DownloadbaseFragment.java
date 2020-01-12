package com.example.kiylx.ti.downloadFragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kiylx.ti.R;
import com.example.kiylx.ti.corebase.DownloadInfo;

import java.util.ArrayList;

public class DownloadbaseFragment extends Fragment {
    private View mRootView;
    private ArrayList<DownloadInfo> mLists;

    @LayoutRes
    private int getresId(){
        return R.layout.downloadbasefragments;
    }

    public DownloadbaseFragment() {
        super();
    }

    public void setLists(ArrayList<DownloadInfo> list){
        this.mLists=list;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(getresId(),null);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }



    private class listAdapter extends RecyclerView.Adapter<DownloadViewHolder>{
        private ArrayList<DownloadInfo> mLists;//recyclerview所用的数据


        void setLists(ArrayList<DownloadInfo> list){
            this.mLists=list;
        }
        @NonNull
        @Override
        public DownloadViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return null;
        }

        @Override
        public void onBindViewHolder(@NonNull DownloadViewHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return 0;
        }
    }

    private class DownloadViewHolder extends RecyclerView.ViewHolder {
        public DownloadViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
