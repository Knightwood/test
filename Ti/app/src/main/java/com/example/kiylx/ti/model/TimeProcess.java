package com.example.kiylx.ti.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TimeProcess {
    static SimpleDateFormat simpleDateFormat;
    public TimeProcess(){
    }
    private static void init(){
        if(simpleDateFormat==null)
        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
    }
    public static String  getTime(){
        init();
        // 获取当前时间
        String time = simpleDateFormat.format(new Date(System.currentTimeMillis()));
        return time;
    }
    public static Date convertToDate(String str){
        init();
        Date date=null;
        try {
            date= simpleDateFormat.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }
    public static String getMonth(String str){
        init();
        Date date= convertToDate(str);
        return null;
    }
    public static Date getThisWeek(){
        init();
        return null;
    }

}
