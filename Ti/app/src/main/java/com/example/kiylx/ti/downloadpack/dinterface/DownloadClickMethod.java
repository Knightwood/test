package com.example.kiylx.ti.downloadpack.dinterface;

import com.example.kiylx.ti.downloadpack.bean.DownloadInfo;

import java.util.List;

/**
 * 这是下载的条目中的databinding所用到的。
 * xml中的按钮调用viewmodel中的方法，再由viewmodel调用这个接口中的方法。
 * 方法的实现是在绑定数据的类中，那个类也就是可以控制下载的类。比如说下载activity。
 * <p>
 * 这个接口被绑定viewmodel的类实现。
 * downloadActivity还会根据不同的fragment放进这个借口的不同的实现。
 */
public interface DownloadClickMethod {

    void download(DownloadInfo info);

    void pause(DownloadInfo info);

    void cancel(DownloadInfo info);

    void reasume(DownloadInfo info);

    float getPercent(DownloadInfo info);
    List<DownloadInfo> getAllDownload();
    List<DownloadInfo> getAllComplete();

    void getAllDownload(List<DownloadInfo> list);
    void getAllComplete(List<DownloadInfo> list);
    boolean isNotWork();
}
