package com.gj.utils;

import com.gj.utils.excel.ExportExcel;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import javax.servlet.http.HttpServletResponse;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author GJ
 * @date 2019-03-12
 * param util
 **/
public class GjUtil {

    /**
     * Checks if a single parameter is null
     *
     * @param str parameter
     * @return True is empty, false is not
     */
    public static boolean paramIsEmpty(Object str) {
        if (str instanceof List) {
            return ((List) str).size() < 1;
        } else {
            return str == null || "".equals(str);
        }
    }

    /**
     * Check multiple parameters for null
     *
     * @param params multiple parameter
     * @return True is contain, false is not
     */
    public static boolean multiParamHasEmpty(List<Object> params) {
        List<Object> paramNullList = params.stream().filter(GjUtil::paramIsEmpty).collect(Collectors.toList());
        return paramNullList.size() > 0;
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
        List<String> listNonNull = list.stream().filter(GjUtil::paramIsNotEmpty).collect(Collectors.toList());
        return listNonNull.size() <= 0 ? null : listNonNull.stream().map(GjUtil::trim).collect(Collectors.toList());
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
        int strLen = 0;
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
     * @apiNote The length of the symbol can only be one
     */
    @SuppressWarnings("unchecked")
    public static String[] spilt(@NonNull String str, @NonNull String symbol) {
        if (paramIsEmpty(str) || symbol.length() > 1) {
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
}
