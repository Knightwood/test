package com.example.kiylx.ti.model;

import com.example.kiylx.ti.KindsofDate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TimeProcess {
    /*
    * 统一获取和存储为string类型，在数据库获取一个日期范围的item时把string转换为date类型*/
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
    public static String[] getWeekorMonth(String arg1,String datenow){
        init();
        String result=null;
        switch (KindsofDate.valueOf(arg1)){
            case THISWEEK:
                break;
            case THISMONTH:
                break;
            case MONTH1:
                break;
            case MONTH2:
                break;
            case MONTH3:
                break;
            case MONTH4:
                break;
            case MONTH5:
                break;

        }
        return new String[]{datenow,result};
    }

}
