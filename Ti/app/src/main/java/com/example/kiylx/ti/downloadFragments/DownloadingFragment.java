package com.example.kiylx.ti.downloadFragments;

import android.view.View;

import com.example.kiylx.ti.corebase.DownloadInfo;

import java.util.ArrayList;

public class DownloadingFragment extends DownloadbaseFragment {


    public static DownloadingFragment getInstance(ArrayList<DownloadInfo> list) {
        return new DownloadingFragment(list);
    }

    public DownloadingFragment(ArrayList<DownloadInfo> list) {
        setDownloadInfoArrayList(list);
    }

    @Override
    public void setDownloadInfoArrayList(ArrayList<DownloadInfo> list) {
        super.setDownloadInfoArrayList(list);
    }

    @Override
    public void bind1(View v, DownloadInfo info) {
        super.bind1(v, info);
    }
}
