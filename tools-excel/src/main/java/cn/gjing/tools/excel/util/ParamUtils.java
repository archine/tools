package cn.gjing.tools.excel.util;

import cn.gjing.tools.excel.exception.ExcelResolverException;

import java.util.Iterator;
import java.util.Map;

/**
 * Param utils
 *
 * @author Gjing
 **/
public final class ParamUtils {
    /**
     * Whether the array contains a value
     *
     * @param arr array
     * @param val value
     * @return boolean
     */
    public static boolean contains(String[] arr, String val) {
        if (arr == null || arr.length == 0) {
            return false;
        }
        for (String o : arr) {
            if (o.equals(val)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Whether it's equal or not
     *
     * @param param1     param1
     * @param param2     param2
     * @param allowEmpty Whether allow empty？
     * @return boolean
     */
    public static boolean equals(Object param1, Object param2, boolean allowEmpty) {
        if (allowEmpty) {
            if (param1 == param2) {
                return true;
            }
            return param1 != null && param1.equals(param2);
        }
        if (param1 == null || "".equals(param1)) {
            return false;
        }
        return param1 == param2 || param1.equals(param2);
    }

    /**
     * Whether obj is null
     *
     * @param obj     obj
     * @param message error message
     * @param <T>     T
     */
    public static <T> void requireNonNull(T obj, String message) {
        if (obj == null) {
            throw new ExcelResolverException(message);
        }
    }

    /**
     * Compute cell formula
     *
     * @param offset   The offset, if you give it to 0, means you start with column A, and 1 means you start with column B
     * @param rowIndex Row index
     * @param colCount How many columns
     * @return If the input parameter is 1,1,10, it means from B1 to K1. Finally return $B$1:$K$1
     */
    public static String createFormula(int offset, int rowIndex, int colCount) {
        char start = (char) ('A' + offset);
        if (colCount <= 25) {
            if (colCount == 0) {
                return "$" + start + "$" + rowIndex;
            } else {
                char end = (char) (start + colCount - 1);
                return "$" + start + "$" + rowIndex + ":$" + end + "$" + rowIndex;
            }
        } else {
            char endPrefix = 'A';
            char endSuffix;
            if ((colCount - 25) / 26 == 0 || colCount == 51) {
                if ((colCount - 25) % 26 == 0) {
                    endSuffix = (char) ('A' + 25);
                } else {
                    endSuffix = (char) ('A' + (colCount - 25) % 26 - 1);
                }
            } else {
                if ((colCount - 25) % 26 == 0) {
                    endSuffix = (char) ('A' + 25);
                    endPrefix = (char) (endPrefix + (colCount - 25) / 26 - 1);
                } else {
                    endSuffix = (char) ('A' + (colCount - 25) % 26 - 1);
                    endPrefix = (char) (endPrefix + (colCount - 25) / 26);
                }
            }
            return "$" + start + "$" + rowIndex + ":$" + endPrefix + endSuffix + "$" + rowIndex;
        }
    }

    /**
     * Delete specified key on HashMap
     *
     * @param map HashMap
     * @param key Specified key
     */
    public static void deleteMapKey(Map<?, ?> map, String key) {
        Iterator<? extends Map.Entry<?, ?>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<?, ?> next = iterator.next();
            if (next.getKey().equals(key)) {
                iterator.remove();
                break;
            }
        }
    }

    /**
     * Check the file type is excel
     *
     * @param fileName file name
     * @return is Excel
     */
    public static boolean isExcel(String fileName) {
        if (fileName == null) {
            return false;
        }
        int pos = fileName.lastIndexOf(".") + 1;
        String extension = fileName.substring(pos);
        return "xls".equals(extension) || "xlsx".equals(extension);
    }
}
