package com.example.kiylx.ti.model;

import com.example.kiylx.ti.KindsofDate;

import org.junit.Test;

import java.util.Calendar;

import static org.junit.Assert.*;

public class TimeProcessTest {

    @Test
    public void getTime() {
        System.out.println(TimeProcess.getTime());
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