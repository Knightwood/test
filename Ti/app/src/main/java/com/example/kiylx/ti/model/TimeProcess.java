package com.example.kiylx.ti.model;

import com.example.kiylx.ti.BR;

import java.text.DateFormat;
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

    /**
     * 初始化操作，生成一个simpleDateFormat对象
     */
    private static void init(){
        if(simpleDateFormat==null)
        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
    }

    /**
     * @return 获取当前的日期，返回日期是String类型
     */
    public static String  getTime(){
        init();
        // 获取当前时间
        String time = simpleDateFormat.format(new Date(System.currentTimeMillis()));
        return time;
    }

    /**
     * @param str 日期字符串
     * @return 把str格式化为Date对象
     */
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

    /**
     * @param kinds 要获取的日期范围
     * @param datenow 当前日期的字符串
     * @return 直接返回一个字符串数组，第一个元素是开始时间，第二个是结束时间
     */
    public static String[] getWeekorMonth_start(KindsofDate kinds, String datenow) {
        init();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(convertToDate(datenow));
        String startDate = null;
        switch (kinds) {
            case THISWEEK:
                calendar.add(Calendar.DAY_OF_MONTH, -7);
                break;
            case THISMONTH:
                calendar.set(Calendar.DAY_OF_MONTH, 1);
                break;
            case MONTH1:
                calendar.add(Calendar.MONTH, -1);
                break;
            case MONTH2:
                calendar.add(Calendar.MONTH, -2);
                break;
            case MONTH3:
                calendar.add(Calendar.MONTH, -3);
                break;
            case MONTH4:
                calendar.add(Calendar.MONTH, -4);
                break;
            case MONTH5:
                calendar.add(Calendar.MONTH, -5);
                break;
        }
        if (kinds==KindsofDate.THISWEEK) {
            calendar.set(Calendar.DAY_OF_MONTH, 1);
        }
            startDate = simpleDateFormat.format(calendar.getTime());

            return new String[]{startDate,getlastday(startDate)};
        }
        public static String getlastday(String startDate){

            Calendar calendar2=Calendar.getInstance();
            calendar2.setTime(convertToDate(startDate));
            calendar2.set(Calendar.DAY_OF_MONTH,0);
            calendar2.add(Calendar.MONTH,1);
        return simpleDateFormat.format(calendar2.getTime());

        }

    }
