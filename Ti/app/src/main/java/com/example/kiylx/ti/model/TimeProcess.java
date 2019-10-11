package com.example.kiylx.ti.model;

import com.example.kiylx.ti.KindsofDate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(convertToDate(datenow));
        String result=null;
        switch (KindsofDate.valueOf(arg1)){
            case THISWEEK:
                calendar.add(Calendar.DAY_OF_MONTH,-7);
                break;
            case THISMONTH:
                calendar.set(Calendar.DAY_OF_MONTH,1);
                break;
            case MONTH1:
                calendar.add(Calendar.MONTH,-1);
                break;
            case MONTH2:
                calendar.add(Calendar.MONTH,-2);
                break;
            case MONTH3:
                calendar.add(Calendar.MONTH,-3);
                break;
            case MONTH4:
                calendar.add(Calendar.MONTH,-4);
                break;
            case MONTH5:
                calendar.add(Calendar.MONTH,-5);
                break;
        }
        result=simpleDateFormat.format(calendar.getTime());
        return new String[]{datenow,result};
    }


}
