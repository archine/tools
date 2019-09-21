package cn.gjing.tools.excel.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Bean工具
 * @author Gjing
 **/
public class BeanUtils {
    /**
     * 查找给定类中声明的方法
     * @param clazz 目标类
     * @param methodName 方法名
     * @param paramTypes 方法参数类型
     * @return Method
     */
    public static Method findDeclaredMethod(Class<?> clazz, String methodName, Class<?>... paramTypes) {
        try {
            return clazz.getDeclaredMethod(methodName, paramTypes);
        } catch (NoSuchMethodException var4) {
            return clazz.getSuperclass() != null ? findDeclaredMethod(clazz.getSuperclass(), methodName, paramTypes) : null;
        }
    }

    /**
     * 设置某个对象的字段的值
     * @param o 字段所在的对象
     * @param field 字段
     * @param value 内容
     */
    public static void setFieldValue(Object o, Field field, Object value) {
        try {
            field.setAccessible(true);
            field.set(o, value);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取字段内的值
     * @param o 字段所在的对象
     * @param field 字段
     * @return 值
     */
    public static Object getFieldValue(Object o, Field field) {
        try {
            field.setAccessible(true);
            return field.get(o);
        } catch (IllegalAccessException e) {
            return null;
        }
    }

}
