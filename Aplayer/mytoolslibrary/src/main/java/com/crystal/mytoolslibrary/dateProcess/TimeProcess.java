package com.crystal.fucktoollibrary.tools.dateProcess;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TimeProcess {
    /*
     *统一获取和存储为string类型，在数据库获取一个日期范围的item时把string转换为date类型
     *y：年
     *M：月
     *d：日
     *h：时（12小时制，0-12）
     *H：时（24小时制，0-23）
     *m：分
     *s：秒
     *S：毫秒
     *E：星期几
     *a：上下午标识
     *
     * */
    static SimpleDateFormat simpleDateFormat;

    public TimeProcess() {
    }

    /**
     * 初始化操作，生成一个simpleDateFormat对象
     */
    private static void init() {
        if (simpleDateFormat == null)
            simpleDateFormat = new SimpleDateFormat("yyyy-mm-dd hh-mm-ss", Locale.CHINA);
    }

    /**
     * @return 获取当前的日期，返回日期是String类型
     */
    public static String getTime() {
        DateFormat format = new SimpleDateFormat("yyyy-mm-dd hh-mm-ss", Locale.CHINA);
        // 获取当前时间
        return format.format(new Date(System.currentTimeMillis()));
    }

    public static String getTime2() {
        DateFormat format = new SimpleDateFormat("yyyy-mm-dd hh-mm-ss", Locale.CHINA);
        return format.format(new Date(System.currentTimeMillis()));
    }

    /**
     * @param str 日期字符串
     * @return 把str格式化为Date对象
     */
    public static Date convertToDate(String str) {
        DateFormat format = new SimpleDateFormat("yyyy-mm-dd hh-mm-ss", Locale.CHINA);
        Date date = null;
        try {
            date = format.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * @param kinds   要获取的日期范围
     * @param datenow 当前日期的字符串
     * @return 直接返回一个字符串数组，第一个元素是开始时间，第二个是结束时间
     */
    public static String[] getWeekorMonth_start(KindsofDate kinds, String datenow) {
        DateFormat format = new SimpleDateFormat("yyyy-mm-dd hh-mm-ss", Locale.CHINA);
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
        if (kinds == KindsofDate.THISWEEK) {
            calendar.set(Calendar.DAY_OF_MONTH, 1);
        }
        startDate = format.format(calendar.getTime());

        return new String[]{startDate, getlastday(startDate)};
    }

    public static String getlastday(String startDate) {
        DateFormat format = new SimpleDateFormat("yyyy-mm-dd hh-mm-ss", Locale.CHINA);
        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTime(convertToDate(startDate));
        calendar2.set(Calendar.DAY_OF_MONTH, 0);
        calendar2.add(Calendar.MONTH, 1);
        return format.format(calendar2.getTime());

    }

}
