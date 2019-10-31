package com.example.kiylx.ti.model;

import com.example.kiylx.ti.DateProcess.KindsofDate;
import com.example.kiylx.ti.DateProcess.TimeProcess;

import org.junit.Test;

public class TimeProcessTest {
    String tmp=null;

    @Test
    public void getTime() {
        System.out.println("获取时间"+ TimeProcess.getTime());
    }

    @Test
    public void convertToDate() {
        System.out.println("转换"+TimeProcess.convertToDate("2019-10-2"));
    }

    @Test
    public void getWeekorMonth_Bookmarkt() {
        for(KindsofDate i:KindsofDate.values()){
            tmp= TimeProcess.getWeekorMonth_Bookmarkt(i.toString(),"2019-10-11");
            System.out.println("开始时间"+tmp);
            getlastday1();
        }
    }


   @Test
    public void getlastday1() {
        String end =TimeProcess.getlastday(tmp);
        System.out.println("结束"+end);

    }
}