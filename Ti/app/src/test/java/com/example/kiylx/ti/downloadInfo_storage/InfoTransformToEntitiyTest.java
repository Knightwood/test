package com.example.kiylx.ti.downloadInfo_storage;

import org.junit.Test;


public class InfoTransformToEntitiyTest {
    private static final String TAG="字符转换测试";

    @Test
    public void test1(){
        String s = "123/569/4576";
        //String[] ss = s.split("/");

        long[] longs= InfoTransformToEntitiy.lString(s);
        String k=InfoTransformToEntitiy.tString(longs);

        for (int i = 0; i <3; i++) {
            System.out.println(longs[i]+" "+k);
        }

    }

}