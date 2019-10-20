package cn.gjing.tools.excel.util;

import cn.gjing.tools.excel.ExcelField;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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

    /**
     * 获取父子类的所有带有注解的字段
     * @param excelClass Excel类
     * @param ignores 忽略的字段
     * @return 带注解的字段
     */
    public static List<Field> getFields(Class<?> excelClass, String[] ignores) {
        //Get all the declared fields
        Field[] declaredFields = excelClass.getDeclaredFields();
        //找到所有带有@ExcelField注解且不为过滤的字段
        List<Field> hasExcelFieldList = Arrays.stream(declaredFields)
                .filter(e -> e.isAnnotationPresent(ExcelField.class))
                .filter(e -> !ParamUtils.contains(ignores, e.getName()))
                .collect(Collectors.toList());
        //如果有父类，父类也加进来
        Class<?> superclass = excelClass.getSuperclass();
        if (superclass != Object.class) {
            hasExcelFieldList.addAll(Arrays.stream(superclass.getDeclaredFields())
                    .filter(e -> e.isAnnotationPresent(ExcelField.class))
                    .filter(e -> !ParamUtils.contains(ignores, e.getName()))
                    .collect(Collectors.toList()));
        }
        return hasExcelFieldList;
    }
}
