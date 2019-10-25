package cn.gjing.tools.excel.util;

/**
 * Param utils
 *
 * @author Gjing
 **/
public class ParamUtils {

    /**
     * Determines whether the array contains a value
     *
     * @param arr array
     * @param val value
     * @return boolean
     */
    public static boolean noContains(String[] arr, String val) {
        for (String o : arr) {
            if (o.equals(val)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Whether it's equal or not
     *
     * @param t param1
     * @param u param2
     * @return boolean
     */
    public static boolean equals(Object t, Object u) {
        return t == u || t.equals(u);
    }

}
