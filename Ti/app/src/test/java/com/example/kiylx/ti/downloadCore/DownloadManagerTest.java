package com.example.kiylx.ti.downloadCore;

import com.example.kiylx.ti.corebase.DownloadInfo;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class DownloadManagerTest {
    private DownloadManager manager;
    private DownloadInfo info1;
    @Before
    public void setUp() throws Exception {
        long blocksize=300;
        manager=DownloadManager.getInstance();
        info1=new DownloadInfo("https://raw.githubusercontent.com/guolindev/eclipse/master/eclipse-inst-win64.exe");
        info1.setContentLength(2400);
        info1.setThreadNum(8);
        manager.processInfo(info1);
        for (int i = 0; i < 8; i++) {
            info1.splitStart[i]+=100;
        }

    }

    @Test
    public void download(){
        try {
            manager.startDownload(info1);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Test
    public void getRate(){
        System.out.println("完成百分比："+manager.getPercentage(info1)+"当前已下载长度："+info1.getTotalLength());

        for (int i = 0; i < 8; i++) {
            System.out.println(info1.splitStart[i]+"--"+info1.splitEnd[i]);
        }

        System.out.println("文件总长度："+info1.getContentLength()+"下载部分"+info1.getTotalLength()/info1.getContentLength());
    }

    @Test
    public void fuck(){
        List<String> list_tmp=new ArrayList<>();
        //list_tmp.add("a");
        list_tmp.addAll(Arrays.asList("a","b","c","d"));
        Iterator<String> iterator=list_tmp.iterator();
        while (iterator.hasNext()){
            System.out.println(iterator.next());
        }
    }
}