package com.example.kiylx.ti.downloadFragments;

import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.kiylx.ti.corebase.DownloadInfo;
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
    @Override
    public void onStart() {
        super.onStart();
        Toast.makeText(getContext(),"下载设置fragment",Toast.LENGTH_LONG).show();
    }
}
