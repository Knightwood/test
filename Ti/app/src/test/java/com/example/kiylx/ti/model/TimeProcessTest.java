package com.example.kiylx.ti.model;

import org.junit.Test;

public class TimeProcessTest {
    String tmp=null;

    @Test
    public void getTime() {
        System.out.println("获取时间"+TimeProcess.getTime());
    }

    @Test
    public void convertToDate() {
        System.out.println("转换"+TimeProcess.convertToDate("2019-10-2"));
    }

    @Test
    public void getWeekorMonth_start() {
        for(KindsofDate i:KindsofDate.values()){
            tmp= TimeProcess.getWeekorMonth_start(i.toString(),"2019-10-11");
            getlastday1();
            System.out.println("开始时间"+tmp);
        }
    }


   @Test
    public void getlastday1() {
        String end =TimeProcess.getlastday(tmp);
        System.out.println("结束"+end);

    }
}