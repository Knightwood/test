package com.example.kiylx.ti.downloadCore;

import com.example.kiylx.ti.corebase.DownloadInfo;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class DownloadManagerTest {
    private DownloadManager manager;
    private DownloadInfo info1;
private List<DownloadInfo> list;
    @Before
    public void setUp() throws Exception {
        list=new ArrayList<>();
        long blocksize=300;
        manager=DownloadManager.getInstance();
        info1=new DownloadInfo("https://raw.githubusercontent.com/guolindev/eclipse/master/eclipse-inst-win64.exe");
        info1.setContentLength(2400);
        info1.setThreadNum(8);
        manager.processInfo(info1);
        for (int i = 0; i < 8; i++) {
            info1.splitStart[i]+=100;
        }

        list.add(info1);

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
        System.out.println("完成百分比："+manager.getPercentage(info1)+"当前已下载长度："+info1.getCurrentLength());

        for (int i = 0; i < 8; i++) {
            System.out.println(info1.splitStart[i]+"--"+info1.splitEnd[i]);
        }

        System.out.println("文件总长度："+info1.getContentLength()+"下载部分"+info1.getCurrentLength()/info1.getContentLength());
    }

    @Test
    public void fuck(){
        List<String> list_tmp=new ArrayList<>();
        //list_tmp.add("a");
        list_tmp.addAll(Arrays.asList("a","b","c","d"));
        Iterator<String> iterator=list_tmp.iterator();

        while (iterator.hasNext()){
            System.out.println(iterator.next());
            iterator.remove();
            System.out.println(iterator.hasNext());
            //结果：a,b,c,d
        }
        /*for (int i = 0; i <list_tmp.size() ; i++) {
            System.out.println(list_tmp.get(i));
            list_tmp.remove(i);
            //结果：a,c
        }*/

    }

    @Test
    public void testRxjava(){
        manager.writeData(list);
    }

}