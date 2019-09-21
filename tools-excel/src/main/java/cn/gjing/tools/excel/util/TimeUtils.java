package cn.gjing.tools.excel.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 时间工具
 * @author Gjing
 **/
public class TimeUtils {
    /**
     * 自定义格式获取文本时间
     *
     * @param date   date
     * @param pattern 格式("yyyy-MM-dd")
     * @return string 文本
     */
    public static String dateToString(Date date, String pattern) {
        SimpleDateFormat format1 = new SimpleDateFormat(pattern);
        return format1.format(date);
    }

    /**
     * 获取时间对象
     *
     * @param date 文本格式时间
     * @return 时间对象
     */
    public static Date stringToDate(String date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            return format.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return new Date();
        }
    }

    /**
     * 获取时间对象
     *
     * @param date 文本格式时间
     * @param pattern 格式
     * @return 时间对象
     */
    public static Date stringToDate(String date,String pattern) {
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        try {
            return format.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return new Date();
        }
    }
}
