package cn.gjing.tools.excel.util;

import cn.gjing.tools.excel.metadata.ExcelType;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
     * MD5 encryption
     *
     * @param body need to encryption
     * @return encrypted string
     */
    public static String encodeMd5(String body) {
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
            return "";
        }
        return buf.toString();
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
            throw new NullPointerException(message);
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
        if (colIndex < 26) {
            return "$" + (char) ('A' + colIndex) + "$" + startRow + ":$" + (char) ('A' + colIndex) + "$" + endRow;
        }
        String all = numberToEn(colIndex);
        return "$" + all + "$" + startRow + ":$" + all + "$" + endRow;
    }

    /**
     * Number to English letter
     *
     * @param number number
     * @return letter
     */
    public static String numberToEn(int number) {
        char prefix = 'A';
        if (number < 26) {
            return "" + (char) ('A' + number);
        }
        char suffix;
        if ((number - 25) % 26 == 0) {
            suffix = (char) (prefix + 25);
            prefix = (char) (prefix + (number - 25) / 26 - 1);
        } else {
            suffix = (char) ('A' + (number - 25) % 26 - 1);
            prefix = (char) (prefix + (number - 25) / 26);
        }
        return "" + prefix + suffix;
    }

    /**
     * Delete specified key on HashMap
     *
     * @param map HashMap
     * @param key Specified key
     */
    public static void deleteMapKey(Map<?, ?> map, Object key) {
        Iterator<? extends Map.Entry<?, ?>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<?, ?> entry = iterator.next();
            if (entry.getKey() == key || entry.getKey().equals(key)) {
                iterator.remove();
                break;
            }
        }
    }

    /**
     * Check the file type is excel
     *
     * @param fileName Excel file name
     * @return Return NULL to indicate that it is not an Excel file
     */
    public static ExcelType getExcelType(String fileName) {
        if (fileName == null) {
            return null;
        }
        int pos = fileName.lastIndexOf(".") + 1;
        String extension = fileName.substring(pos);
        if ("xls".equals(extension)) {
            return ExcelType.XLS;
        }
        if ("xlsx".equals(extension)) {
            return ExcelType.XLSX;
        }
        return null;
    }
}
