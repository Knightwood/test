package com.example.kiylx.ti.downloadFragments;

import android.util.Log;
import android.view.View;

import com.example.kiylx.ti.corebase.DownloadInfo;
import com.example.kiylx.ti.myFragments.RecyclerViewBaseFragment;
import com.example.kiylx.ti.myInterface.DownloadClickMethod;

public class DownloadSettingFragment extends RecyclerViewBaseFragment {
    private static final String TAG="下载系列fragment";
    public DownloadSettingFragment(DownloadClickMethod method) {
        super();
        Log.d(TAG, "DownloadSettingFragment: ");
    }

    @Override
    public void bindItemView(View v, DownloadInfo info) {

    }
}
