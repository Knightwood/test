package com.example.kiylx.ti.downloadInfo_storage;

import com.example.kiylx.ti.corebase.DownloadInfo;

public class InfoTransformToEntitiy {
    public static DownloadEntity transformInfo(DownloadInfo info) {
        return new DownloadEntity(info.getUrl(), info.getFileName(), info.getPath()
                , info.getBlockCompleteNum(), info.getBlockPauseNum(), info.getPauseFlags(),
                info.getThreadNum(), info.getContentLength(), info.getTotalProcress(),
                info.getBlockSize(), info.getDownloadSuccess());
    }
}
