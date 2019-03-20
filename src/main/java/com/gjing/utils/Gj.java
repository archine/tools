package com.gjing.utils;

import com.gjing.utils.excel.ExportExcel;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.MultiValueMap;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Array;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Archine
 * param util
 **/
public final class Gj {
    /**
     * Checks if a single parameter is null
     *
     * @param str parameter
     * @return True is empty, false is not
     */
    public static boolean paramIsEmpty(Object str) {
        if (str instanceof List) {
            return ((List) str).isEmpty();
        } else if (str instanceof Map) {
            return ((Map) str).isEmpty();
        } else if (str instanceof Array) {
            return Array.getLength(str) == 0;
        } else {
            return str == null || "".equals(str);
        }
    }

    /**
     * Check multiple parameters for null
     *
     * @param params multiple parameter
     * @param <T>    parameter type
     * @return True is contain, false is not
     */
    public static <T> boolean multiParamHasEmpty(List<T> params) {
        List<T> paramNullList = params.stream().filter(Gj::paramIsEmpty).collect(Collectors.toList());
        return !paramNullList.isEmpty();
    }

    /**
     * Check multiple parameters for not null
     *
     * @param str parameter
     * @return True is not, false is empty
     */
    public static boolean paramIsNotEmpty(Object str) {
        return !paramIsEmpty(str);
    }

    /**
     * remove null value for the param
     *
     * @param str param
     * @return NonNull param
     */
    public static String trim(String str) {
        return paramIsEmpty(str) ? null : str.trim();
    }

    /**
     * remove null values for the list
     *
     * @param list A list of null values that need to be removed
     * @return NonNull List
     */
    public static List<String> trim(List<String> list) {
        List<String> listNonNull = list.stream().filter(Gj::paramIsNotEmpty).collect(Collectors.toList());
        return listNonNull.size() <= 0 ? null : listNonNull.stream().map(Gj::trim).collect(Collectors.toList());
    }

    /**
     * change to upper case
     *
     * @param str parameter that need to change to upper case
     * @return changed param
     */
    public static String toUpperCase(@NonNull String str) {
        return paramIsEmpty(str) ? null : str.toUpperCase();
    }

    /**
     * change to lower case
     *
     * @param str parameter that need to change to lower case
     * @return changed param
     */
    public static String toLowerCase(@NonNull String str) {
        return paramIsEmpty(str) ? null : str.toLowerCase();
    }

    /**
     * remove the symbol that parameter
     *
     * @param str    parameter
     * @param symbol symbol
     * @return has been removed
     */
    public static String removeSymbol(String str, @Nullable String symbol) {
        if (paramIsEmpty(str)) {
            return null;
        } else {
            str = removeStartSymbol(str, symbol);
            return removeEndSymbol(str, symbol);
        }
    }

    /**
     * remove the start symbol that parameter
     *
     * @param str    parameter
     * @param symbol symbol
     * @return has been removed
     */
    public static String removeStartSymbol(String str, String symbol) {
        int strLen;
        if (paramIsNotEmpty(str) && (strLen = str.length()) != 0) {
            int start = 0;
            if (paramIsEmpty(symbol)) {
                return trim(str);
            } else {
                while (start != strLen && symbol.indexOf(str.charAt(start)) != -1) {
                    start++;
                }
            }
            return str.substring(start);
        }
        return trim(str);
    }

    /**
     * remove the end symbol that parameter
     *
     * @param str    parameter
     * @param symbol symbol
     * @return has been removed
     */
    public static String removeEndSymbol(String str, String symbol) {
        int end;
        if (paramIsNotEmpty(str) && (end = str.length()) != 0) {
            if (paramIsEmpty(symbol)) {
                return trim(str);
            }
            while (end != 0 && symbol.indexOf(str.charAt(end - 1)) != -1) {
                end--;
            }
            return str.substring(0, end);
        }
        return trim(str);
    }

    /**
     * separated by symbols
     *
     * @param str    need to separated parameter
     * @param symbol symbol
     * @return has been separated
     * warning: The length of the symbol can only be one
     */
    @SuppressWarnings("unchecked")
    public static String[] split(String str, String symbol) {
        if (paramIsEmpty(str) || symbol.length() != 1) {
            return null;
        } else {
            List<String> list = new ArrayList();
            int i = 0;
            int start = 0;
            while (i < str.length()) {
                if (String.valueOf(str.charAt(i)).equals(symbol)) {
                    list.add(str.substring(start, i));
                    i++;
                    start = i;
                } else {
                    i++;
                }
            }
            list.add(str.substring(start));
            return list.toArray(new String[0]);
        }
    }

    /**
     * MD5 encryption
     *
     * @param body need to encryption
     * @return encrypted string
     */
    public static String md5(@NonNull String body) {
        StringBuilder buf = new StringBuilder();
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(body.getBytes());
            byte[] b = md.digest();
            int i;
            for (byte b1 : b) {
                i = b1;
                if (i < 0) {
                    i += 256;
                }
                if (i < 16) {
                    buf.append("0");
                }
                buf.append(Integer.toHexString(i));
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
        return buf.toString();
    }

    /**
     * sha256 Hmac加密
     *
     * @param str    需要加密的消息
     * @param secret 秘钥
     * @return 加密后的字符串
     */
    public static String sha256Hmac(String str, String secret) {
        String hash = "";
        try {
            Mac sha256Hmac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
            sha256Hmac.init(secretKey);
            byte[] bytes = sha256Hmac.doFinal(str.getBytes());
            hash = byteArrayToHexString(bytes);
        } catch (Exception e) {
            System.out.println("Error HmacSHA256 ====" + e.getMessage());
        }
        return hash;
    }


    /**
     * 将加密后的字节数组转换成字符串
     *
     * @param b 字节数组
     * @return 字符串
     */
    private static String byteArrayToHexString(byte[] b) {
        StringBuilder hs = new StringBuilder();
        String stmp;
        for (int n = 0; b != null && n < b.length; n++) {
            stmp = Integer.toHexString(b[n] & 0XFF);
            if (stmp.length() == 1) {
                hs.append('0');
            }
            hs.append(stmp);
        }
        return hs.toString().toLowerCase();
    }

    /**
     * Excel export
     *
     * @param response response
     * @param list     Data that needs to be exported
     * @param headers  excel headers
     * @param title    Excel file name
     * @param info     Excel introduction, Pass null or "" if you don't need it
     */
    public static void excelExport(HttpServletResponse response, List<Object[]> list, String[] headers, String title, @Nullable String info) {
        ExportExcel.generateHaveExcelName(response, list, headers, title, info);
    }

    /**
     * Determines whether an array contains other arrays
     *
     * @param t target array
     * @param u param
     * @return true is contains
     */
    public static boolean contains(String[] t, String u) {
        if (t.length < 1) {
            return false;
        }
        if (Gj.paramIsEmpty(u)) {
            return false;
        }
        for (String t1 : t) {
            if (t1.equals(u)) {
                return true;
            }
        }
        return false;
    }

    /**
     * get text for time
     *
     * @param date date
     * @return string
     */
    public static String getDateAsString(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(date);
    }

    /**
     * get custom format text for time
     *
     * @param date   date
     * @param format format
     * @return string
     */
    public static String getDateAsString(Date date, String format) {
        SimpleDateFormat format1 = new SimpleDateFormat(format);
        return format1.format(format);
    }

    /**
     * get java for time
     *
     * @param date text for time
     * @return java date
     * @throws ParseException format exception
     */
    public static Date getDate(String date) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.parse(date);
    }

    /**
     * get custom format java for time
     *
     * @param date   date
     * @param format format
     * @return java for time
     * @throws ParseException format ex
     */
    public static Date getDate(String date, String format) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        return dateFormat.parse(date);
    }

    /**
     * text for time change to date
     *
     * @param str text for time
     * @return date
     * @throws ParseException format EX
     */
    public static Calendar dateToCalendar(String str) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(simpleDateFormat.parse(str));
        return calendar;
    }

    /**
     * url拼接
     *
     * @param map 参数
     * @param url 需要拼接的url
     * @return 拼接完后的url
     */
    public static String urlAppend(String url, MultiValueMap<String, String> map) {
        StringBuilder builder = new StringBuilder();
        builder.append(url).append("?");
        for (String s : map.keySet()) {
            if (map.size() == 1) {
                builder.append(s).append("=").append("{").append(s).append("}");
            } else {
                builder.append(s).append("=").append("{").append(s).append("}&");
            }
        }
        return builder.toString().substring(0, builder.toString().length() - 1);
    }

    /**
     * url append , example: http://ip:port?param1={param1}
     * @param map param
     * @param url url
     * @return appended url
     */
    public static String urlAppend(String url, Map<String, Object> map) {
        StringBuilder builder = new StringBuilder();
        builder.append(url).append("?");
        for (String s : map.keySet()) {
            if (map.size() == 1) {
                builder.append(s).append("=").append("{").append(s).append("}");
            } else {
                builder.append(s).append("=").append("{").append(s).append("}&");
            }
        }
        if (map.size() == 1) {
            return builder.toString();
        } else {
            return builder.toString().substring(0, builder.toString().length() - 1);
        }
    }
}
