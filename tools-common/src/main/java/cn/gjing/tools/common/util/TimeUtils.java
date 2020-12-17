package cn.gjing.tools.common.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

/**
 * @author Gjing
 **/
public final class TimeUtils {
    public static SimpleDateFormat DATE_FORMAT;
    public static SimpleDateFormat DATE_TIME_FORMAT;
    public static SimpleDateFormat TIME_FORMAT;
    public static SimpleDateFormat MINUTE_FORMAT;

    static {
        DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
        DATE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        MINUTE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        TIME_FORMAT = new SimpleDateFormat("HH:mm:ss");
    }

    private TimeUtils() {

    }

    /**
     * 获取文本格式时间
     *
     * @param date date
     * @return 文本时间
     */
    public static String toText(Date date) {
        return DATE_FORMAT.format(date);
    }

    /**
     * 获取文本时间
     *
     * @param date   时间
     * @param format format
     * @return 文本时间
     */
    public static String toText(Date date, SimpleDateFormat format) {
        return format.format(date);
    }

    /**
     * LocalDate转化为指定格式字符串
     *
     * @param localDate localDate
     * @return .
     */
    public static String toText(LocalDate localDate) {
        return localDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    /**
     * LocalDateTime转化为指定格式字符串
     *
     * @param localDateTime LocalDateTime
     * @return 时间字符串
     */
    public static String toText(LocalDateTime localDateTime) {
        return localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    /**
     * LocalTime转化为指定格式字符串
     *
     * @param localTime localTime
     * @return 时间字符串
     */
    public static String toText(LocalTime localTime) {
        return localTime.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    }

    /**
     * 日期转文本时间
     *
     * @param calendar 日期
     * @param format   格式
     * @return 文本时间
     */
    public static String toText(Calendar calendar, String format) {
        return toText(calendar.getTime(), format);
    }

    /**
     * 将时间戳转换为文本时间
     *
     * @param timeStamp 时间戳
     * @return 文本时间
     */
    public static String toText(Long timeStamp) {
        return toText(new Date(timeStamp));
    }

    /**
     * 将时间戳转换为文本时间
     *
     * @param timeStamp 时间戳
     * @param format    格式
     * @return 文本时间
     */
    public static String toText(Long timeStamp, String format) {
        return toText(new Date(timeStamp), format);
    }

    /**
     * 将时间戳转换为文本时间
     *
     * @param timeStamp 时间戳
     * @param format    格式
     * @return 文本时间
     */
    public static String toText(Long timeStamp, SimpleDateFormat format) {
        return toText(new Date(timeStamp), format);
    }

    /**
     * 自定义格式获取文本时间
     *
     * @param date   date
     * @param format 格式("yyyy-MM-dd")
     * @return string 文本
     */
    public static String toText(Date date, String format) {
        SimpleDateFormat format1 = new SimpleDateFormat(format);
        return format1.format(date);
    }

    /**
     * 获取文本格式时间
     *
     * @param date date
     * @return 文本时间
     */
    public static String toText2(Date date) {
        return DATE_TIME_FORMAT.format(date);
    }

    /**
     * 获取时间对象
     *
     * @param date 文本格式时间
     * @return 时间对象
     */
    public static Date toDate(String date) {
        try {
            return DATE_FORMAT.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return new Date();
        }
    }

    /**
     * 获取时间对象
     *
     * @param date 文本格式时间
     * @return 时间对象
     */
    public static Date toDate2(String date) {
        try {
            return DATE_TIME_FORMAT.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return new Date();
        }
    }

    /**
     * 获取时间对象
     *
     * @param date   文本格式时间
     * @param format format
     * @return 时间对象
     */
    public static Date toDate(String date, SimpleDateFormat format) {
        try {
            return format.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return new Date();
        }
    }

    /**
     * 自定义格式获取时间对象
     *
     * @param date   文本时间
     * @param format format 转化格式
     * @return 时间对象
     */
    public static Date toDate(String date, String format) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        try {
            return dateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return new Date();
        }
    }

    /**
     * localDateTime转date
     *
     * @param dateTime local
     * @return date
     */
    public static Date toDate(LocalDateTime dateTime) {
        return Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * localDate转date
     *
     * @param localDate localDate
     * @return date
     */
    public static Date toDate(LocalDate localDate) {
        return toDate(localDate.atStartOfDay());
    }

    /**
     * 日期转时间
     *
     * @param calendar 日期
     * @param format   格式
     * @return 时间
     */
    public static Date toDate(Calendar calendar, String format) {
        return toDate(toText(calendar, format));
    }

    /**
     * date 转localDateTime
     *
     * @param date date
     * @return localDateTime
     */
    public static LocalDateTime toLocalDateTime(Date date) {
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }

    /**
     * date 转localDate
     *
     * @param date date
     * @return localDate
     */
    public static LocalDate toLocalDate(Date date) {
        return toLocalDateTime(date).toLocalDate();
    }

    /**
     * 文本日期转LocalDate
     *
     * @param date date
     * @return localDate
     */
    public static LocalDate toLocalDate(String date) {
        return LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    /**
     * 文本日期转LocalDateTime
     *
     * @param dateTime dateTime
     * @return LocalDateTime
     */
    public static LocalDateTime toLocalDateTime(String dateTime) {
        return LocalDateTime.parse(dateTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    /**
     * 获得时间戳
     *
     * @param localDateTime lo
     * @return .
     */
    public static long toTimestamp(LocalDateTime localDateTime) {
        ZoneId zone = ZoneId.systemDefault();
        Instant instant = localDateTime.atZone(zone).toInstant();
        return instant.toEpochMilli();
    }

    /**
     * Long类型时间戳转化为LocalDateTime
     *
     * @param timestamp 时间戳
     * @return LocalDateTime
     */
    public static LocalDateTime toLocalDateTime(Long timestamp) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault());
    }

    /**
     * 将文本时间转换为时间戳
     *
     * @param date date
     * @return 时间戳
     */
    public static Long toTimestamp(String date) {
        return toDate(date).getTime();
    }

    /**
     * 将文本时间转换为时间戳
     *
     * @param date date
     * @return 时间戳
     */
    public static Long toTimestamp2(String date) {
        return toDate2(date).getTime();
    }

    /**
     * 将文本时间转换为时间戳
     *
     * @param date date
     * @return 时间戳
     */
    public static Long toTimestamp(String date, SimpleDateFormat format) {
        return toDate(date, format).getTime();
    }

    /**
     * 将文本时间转换为时间戳
     *
     * @param date date
     * @param format 转换格式
     * @return 时间戳
     */
    public static Long toTimestamp(String date, String format) {
        return toDate(date, format).getTime();
    }

    /**
     * 文本时间转化为日期
     *
     * @param date 文本时间
     * @return 日期
     */
    public static Calendar toCalendar(String date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat();
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(simpleDateFormat.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return calendar;
    }

    /**
     * 查询一个日期(年月日)到目前过了多少年
     *
     * @param date 开始日期
     * @return 相差数量
     */
    public static Integer getYearsByStartTime(String date) {
        LocalDate startDate1 = toLocalDate(date);
        LocalDate currentDate = LocalDate.now();
        if (currentDate.isBefore(startDate1)) {
            return 0;
        } else {
            return startDate1.until(currentDate).getYears();
        }
    }

    /**
     * 查询一个日期(年月日)到目前过了多少年
     *
     * @param date 开始时间
     * @return 相差数量
     */
    public static Integer getYearsByStartTime(LocalDate date) {
        LocalDate currentDate = LocalDate.now();
        if (currentDate.isBefore(date)) {
            return 0;
        } else {
            return date.until(currentDate).getYears();
        }
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
     *
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
     *
     * @param date 日期
     * @param n    要增加的月数
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
     *
     * @param date 日期
     * @param n    要增加的天数
     * @return date
     */
    public static Date addDay(Date date, int n) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, n);
        return cal.getTime();
    }

    /**
     * 获取两个日期（不含时分秒）相差的天数，不包含今天
     *
     * @param startDate 开始时间
     * @param endDate   结束时间
     * @return 相差的天数
     */
    public static int dateBetween(String startDate, String endDate) {
        Date dateStart = toDate(startDate);
        Date dateEnd = toDate(endDate);
        return (int) ((dateEnd.getTime() - dateStart.getTime()) / 1000 / 60 / 60 / 24);
    }

    /**
     * 获取两个日期（不含时分秒）相差的天数，不包含今天
     *
     * @param startDate 开始时间
     * @param endDate   结束时间
     * @return 相差的天数
     */
    public static int dateBetween(Date startDate, Date endDate) {
        return (int) ((startDate.getTime() - endDate.getTime()) / 1000 / 60 / 60 / 24);
    }

    /**
     * 获取两个日期（不含时分秒）相差的天数，包含今天
     *
     * @param startDate 开始时间
     * @param endDate   结束时间
     * @return 相差的天数
     */
    public static int dateBetweenIncludeToday(String startDate, String endDate) {
        return dateBetween(startDate, endDate) + 1;
    }

    /**
     * 获取两个日期（不含时分秒）相差的天数，包含今天
     *
     * @param startDate 开始时间
     * @param endDate   结束时间
     * @return 相差的天数
     */
    public static int dateBetweenIncludeToday(Date startDate, Date endDate) {
        return dateBetween(startDate, endDate) + 1;
    }
}
