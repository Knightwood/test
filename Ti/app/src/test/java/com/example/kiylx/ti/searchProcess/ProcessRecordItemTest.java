package com.example.kiylx.ti.searchProcess;

import android.util.Log;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @创建者 kiylx
 * @创建时间 2020/2/13{TIME}
 */
public class ProcessRecordItemTest {
    private static final String TAG="网址测试";

    List<String> arrayList = new ArrayList<>();
    Boolean result[];
    @Before
    public void setUp() {
       result =new Boolean[20];
            arrayList.add("http://www.zhihu.com");
            arrayList.add("http://baidu.com");
            arrayList.add("www.baidu.com");
            arrayList.add("https://www.bilibili.com");
            arrayList.add("baidu.com");
            arrayList.add("3.13");
            arrayList.add("baidu.90");
            arrayList.add("100.com");
            arrayList.add("https:baidu.cn");


    }
    @Test
    public void testPex(){
        for (int i = 0; i <arrayList.size() ; i++) {
            result[i]= ProcessRecordItem.testPex(arrayList.get(i));
        }
        for (int i = 0; i < 9; i++) {
            System.out.printf("输入"+ arrayList.get(i));
            System.out.printf("结果"+ result[i]+"\n");

        }

    }
    @Test
    public void test2(){
        String url[]=new String[9];
        for (int i=0;i<arrayList.size();i++){
            url[i]=ProcessRecordItem.converKeywordLoadOrSearch(arrayList.get(i));
        }
        for (int i = 0; i < 9; i++) {
            System.out.println("结果"+url[i]+"\n");
        }
    }

}