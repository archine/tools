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
     * Horizontal creation formula
     *
     * @param startColIndex Start column index
     * @param rowIndex      Row index
     * @param endColIndex   End column index
     * @return If the input parameter is 1,1,10, return $B$1:$K$1
     */
    public static String createFormulaX(int startColIndex, int rowIndex, int endColIndex) {
        char start = (char) ('A' + startColIndex);
        if (endColIndex <= 25) {
            if (endColIndex == 0) {
                return "$" + start + "$" + rowIndex;
            } else {
                char end = (char) (start + endColIndex - 1);
                return "$" + start + "$" + rowIndex + ":$" + end + "$" + rowIndex;
            }
        } else {
            char endPrefix = 'A';
            char endSuffix;
            if ((endColIndex - 25) % 26 == 0) {
                endSuffix = (char) ('A' + 25);
                endPrefix = (char) (endPrefix + (endColIndex - 25) / 26 - 1);
            } else {
                endSuffix = (char) ('A' + (endColIndex - 25) % 26 - 1);
                endPrefix = (char) (endPrefix + (endColIndex - 25) / 26);
            }
            return "$" + start + "$" + rowIndex + ":$" + endPrefix + endSuffix + "$" + rowIndex;
        }
    }

    /**
     * Vertical creation formula
     *
     * @param colIndex colIndex
     * @param startRow start row index
     * @param endRow   end row index
     * @return If the input parameter is 1,2,5, return $B$2:$B$5
     */
    public static String createFormulaY(int colIndex, int startRow, int endRow) {
        char prefix = 'A';
        if (colIndex < 26) {
            return "$" + (char) ('A' + colIndex) + "$" + startRow + ":$" + (char) ('A' + colIndex) + "$" + endRow;
        }
        char suffix;
        if ((colIndex - 25) % 26 == 0) {
            suffix = (char) (prefix + 25);
            prefix = (char) (prefix + (colIndex - 25) / 26 - 1);
        } else {
            suffix = (char) ('A' + (colIndex - 25) % 26 - 1);
            prefix = (char) (prefix + (colIndex - 25) / 26);
        }
        return "$" + prefix + suffix + "$" + startRow + ":$" + prefix + suffix + "$" + endRow;
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
