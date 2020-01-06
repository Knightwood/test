package com.example.kiylx.ti.myInterface;

import com.example.kiylx.ti.corebase.DownloadInfo;

public interface DownloadInterfaceImpl {
    /**
     * 开启下载服务，并绑定服务（混合绑定），以此保证服务在后台运行：
     * 即使downloadActivivty结束也可以继续在后台运行
     * 根据info开始下载
     */
    void startDownoadService(DownloadInfo info);
}
