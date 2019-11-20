package com.example.kiylx.ti.DownloadCore;

import com.example.kiylx.ti.Corebase.DownloadInfo;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class DownloadManagerTest {
    DownloadManager manager;
    DownloadInfo info1;
    @Before
    public void setUp() throws Exception {
        manager=DownloadManager.getInstance();
        info1=new DownloadInfo("https://raw.githubusercontent.com/guolindev/eclipse/master/eclipse-inst-win64.exe",null,null,8);
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