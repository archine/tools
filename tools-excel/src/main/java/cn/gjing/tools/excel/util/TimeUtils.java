package cn.gjing.tools.excel.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Time utils
 * @author Gjing
 **/
public class TimeUtils {
    /**
     * Gets the text time in a custom format
     *
     * @param date    date
     * @param pattern Format, such as yyyy-mm-dd
     * @return string String
     */
    public static String dateToString(Date date, String pattern) {
        SimpleDateFormat format1 = new SimpleDateFormat(pattern);
        return format1.format(date);
    }

    /**
     * Get time object
     *
     * @param date String date
     * @return Date
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
     * Get time object
     *
     * @param date    String date
     * @param pattern Format, such as yyyy-mm-dd
     * @return Date
     */
    public static Date stringToDate(String date, String pattern) {
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        try {
            return format.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return new Date();
        }
    }
}
