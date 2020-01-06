package com.example.kiylx.ti.downloadCore;

import com.example.kiylx.ti.corebase.DownloadInfo;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

public class DownloadManagerTest {
    private DownloadManager manager;
    private DownloadInfo info1;
    @Before
    public void setUp() throws Exception {
        manager=DownloadManager.getInstance();
        info1=new DownloadInfo("https://raw.githubusercontent.com/guolindev/eclipse/master/eclipse-inst-win64.exe");
    }

    @Test
    public void download(){
        try {
            manager.startDownload(info1);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}