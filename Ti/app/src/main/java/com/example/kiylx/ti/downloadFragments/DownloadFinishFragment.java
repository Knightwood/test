package com.example.kiylx.ti.downloadFragments;

import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.kiylx.ti.corebase.DownloadInfo;
import com.example.kiylx.ti.myInterface.DownloadClickMethod;

public class DownloadFinishFragment extends RecyclerViewBaseFragment {
    private static final String TAG="下载系列fragment";
    /**
     * @return 提供不同于默认的下载item的item视图
     */
    @Override
    public int getresId() {
        return super.getresId();
    }

    public DownloadFinishFragment(DownloadClickMethod method) {
        super();
        Log.d(TAG, "DownloadFinishFragment: ");
    }

    @Override
    public void bindItemView(View v, DownloadInfo info) {

    }
    @Override
    public void onStart() {
        super.onStart();
        Toast.makeText(getContext(),"下载完成fragment",Toast.LENGTH_LONG).show();
    }
}
