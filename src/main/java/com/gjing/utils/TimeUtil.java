package com.gjing.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author Archine
 **/
public class TimeUtil {
    /**
     * 获取文本格式时间
     *
     * @param date date
     * @return 文本时间
     */
    public static String getDateAsString(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(date);
    }

    /**
     * 自定义格式获取文本时间
     *
     * @param date   date
     * @param format 格式("yyyy-MM-dd")
     * @return string 文本
     */
    public static String getDateAsString(Date date, String format) {
        SimpleDateFormat format1 = new SimpleDateFormat(format);
        return format1.format(format);
    }

    /**
     * 获取时间对象
     *
     * @param date 文本格式时间
     * @return 时间对象
     * @throws ParseException format exception
     */
    public static Date getDate(String date) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.parse(date);
    }

    /**
     * 自定义格式获取时间对象
     *
     * @param date   文本时间
     * @param format format 转化格式
     * @return 时间对象
     * @throws ParseException format ex
     */
    public static Date getDate(String date, String format) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        return dateFormat.parse(date);
    }

    /**
     * 文本时间转化为日期
     *
     * @param str 文本时间
     * @return 日期
     * @throws ParseException format EX
     */
    public static Calendar dateToCalendar(String str) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(simpleDateFormat.parse(str));
        return calendar;
    }


    /**
     * 获取日期时间当月的总天数，如2017-02-13，返回28
     *
     * @param date 时间对象
     * @return days
     */
    public static int getAllDaysOfMonth(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.getActualMaximum(Calendar.DATE);
    }

    /**
     * 获取日期时间的天数，如2017-02-13，返回13
     *
     * @param date 时间对象
     * @return 如2017-02-13，返回13
     */
    public static int getDays(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.DATE);
    }

    /**
     * 获取日期时间的年份，如2017-02-13，返回2017
     *
     * @param date 时间对象
     * @return years
     */
    public static int getYears(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.YEAR);
    }

    /**
     * 获取日期时间的月份，如2017年2月13日，返回2
     * @param date 时间对象
     * @return month
     */
    public static int getMonth(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.MONTH) + 1;
    }

    /**
     * 在日期上增加数个整月
     * @param date 日期
     * @param n 要增加的月数
     * @return date
     */
    public static Date addMonth(Date date, int n) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MONTH, n);
        return cal.getTime();
    }

    /**
     * 在日期上增加天数
     * @param date 日期
     * @param n 要增加的天数
     * @return date
     */
    public static Date addDay(Date date, int n) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, n);
        return cal.getTime();
    }

    /**
     * 将时间转换为时间戳
     * @param stringDate 文本时间
     * @return 时间戳
     */
    public static String stringDateToStamp(String stringDate) throws ParseException{
        return String.valueOf(TimeUtil.getDate(stringDate).getTime());
    }

    /**
     * 将时间戳转换为时间
     * @param timeStamp 时间戳
     * @return 文本时间
     */
    public static String stampToDate(String timeStamp){
        long l = Long.parseLong(timeStamp);
        return TimeUtil.getDateAsString(new Date(l));
    }
}
