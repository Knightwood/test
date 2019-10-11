package com.example.kiylx.ti.model;

import org.junit.Test;

public class TimeProcessTest {

    @Test
    public void getTime() {
        System.out.println("获取时间"+TimeProcess.getTime());
    }

    @Test
    public void convertToDate() {
        System.out.println("转换"+TimeProcess.convertToDate("2019-10-2"));
    }

    @Test
    public void getWeekorMonth() {
        for(KindsofDate i:KindsofDate.values()){
            String[] tmp= TimeProcess.getWeekorMonth(i.toString(),"2019-10-11");
            System.out.println(tmp[0]+"//"+tmp[1]);

        }
    }
}