package com.gjing.utils;

import org.springframework.lang.NonNull;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Archine
 * param util
 **/
public final class ParamUtil {
    /**
     * 检查参数是否为空
     *
     * @param str 参数
     * @return True 为空,false不为空
     */
    public static<T> boolean paramIsEmpty(T str) {
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
     * 检查多参数里面是否有空值
     *
     * @param params 多个参数集合
     * @param <T>    参数类型,泛型
     * @return true为包括,false不包括
     */
    public static <T> boolean multiParamHasEmpty(List<T> params) {
        List<T> paramNullList = params.stream().filter(ParamUtil::paramIsEmpty).collect(Collectors.toList());
        return !paramNullList.isEmpty();
    }

    /**
     * 参数不为空或者size=0或者isEmpty
     * @param str 参数
     * @return true为不含有,false为含有
     */
    public static boolean paramIsNotEmpty(Object str) {
        return !paramIsEmpty(str);
    }

    /**
     * 去除字符串的空格
     * @param str 字符串
     * @return 去除后
     */
    public static String trim(String str) {
        return paramIsEmpty(str) ? null : str.trim();
    }

    /**
     * 去除集合中的空元素
     *
     * @param list 集合
     * @return 不包含空值的集合
     */
    public static List<String> trim(List<String> list) {
        List<String> listNonNull = list.stream().filter(ParamUtil::paramIsNotEmpty).collect(Collectors.toList());
        return listNonNull.size() <= 0 ? null : listNonNull.stream().map(ParamUtil::trim).collect(Collectors.toList());
    }

    /**
     * 大写
     *
     * @param str 字符串
     * @return 大写
     */
    public static String toUpperCase(@NonNull String str) {
        return paramIsEmpty(str) ? null : str.toUpperCase();
    }

    /**
     * 小写
     *
     * @param str 字符串
     * @return 小写字符串
     */
    public static String toLowerCase(@NonNull String str) {
        return paramIsEmpty(str) ? null : str.toLowerCase();
    }

    /**
     * 移除字符串的符号
     *
     * @param str 字符串
     * @param symbol 符号
     * @return 移除后
     */
    public static String removeSymbol(String str, String symbol) {
        if (paramIsEmpty(str)) {
            return null;
        } else {
            str = removeStartSymbol(str, symbol);
            return removeEndSymbol(str, symbol);
        }
    }

    /**
     * 移除字符串开始的符号
     *
     * @param str 字符串
     * @param symbol 符号
     * @return 移除后的
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
     * 移除字符串末尾的符号
     *
     * @param str 需要处理的字符串
     * @param symbol symbol 符号
     * @return 移除后的文本
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
     * 字符串根据符号截取
     *
     * @param str    需要截取的字符串
     * @param symbol 符号
     * @return 截取完的文本数组
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
     * 判断数组里是否包含指定值
     *
     * @param t 目标数组
     * @param u param 值
     * @return 返回true为包含
     */
    public static boolean contains(String[] t, String u) {
        if (t.length < 1) {
            return false;
        }
        if (ParamUtil.paramIsEmpty(u)) {
            return false;
        }
        for (String t1 : t) {
            if (t1.equals(u)) {
                return true;
            }
        }
        return false;
    }
}
