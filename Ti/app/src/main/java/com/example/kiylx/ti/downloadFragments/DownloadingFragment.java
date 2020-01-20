package com.example.kiylx.ti.downloadFragments;

import android.view.View;

import com.example.kiylx.ti.corebase.DownloadInfo;
import com.example.kiylx.ti.myInterface.DownloadClickMethod;

import java.util.List;

public class DownloadingFragment extends DownloadbaseFragment {


    public static DownloadingFragment getInstance(List<DownloadInfo> list, DownloadClickMethod minterface) {
        return new DownloadingFragment(minterface,list);
    }

    public DownloadingFragment(DownloadClickMethod minterface, List<DownloadInfo> list ) {
        super(minterface);
        setDownloadInfoArrayList(list);
    }

    //重写方法以达到除了传入其他的list之外处理其他的逻辑
    @Override
    public void setDownloadInfoArrayList(List<DownloadInfo> list) {
        super.setDownloadInfoArrayList(list);
    }

    //重写的viewholder中的bind方法
    @Override
    public void bind1(View v, DownloadInfo info) {
        super.bind1(v, info);
    }
}
