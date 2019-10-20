package cn.gjing.tools.common.util;

import cn.gjing.tools.common.exception.CastException;
import org.springframework.stereotype.Component;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Gjing
 **/
@Component
@SuppressWarnings("unused")
public class BeanUtils {
    /**
     * 复制属性
     *
     * @param source  原对象
     * @param target  目标对象
     * @param ignores 忽略的字段
     */
    public static void copyProperties(Object source, Object target, String... ignores) {
        BeanUtils.copyProperties(source, target, ignores);
    }

    /**
     * 复制属性
     *
     * @param source  原对象
     * @param target  目标对象
     * @param ignores 忽略的字段
     * @param <T>     T
     * @return T
     */
    public static <T> T copyProperties(Object source, Class<T> target, String... ignores) {
        T t = null;
        try {
            t = target.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        assert t != null;
        if (ignores == null) {
            BeanUtils.copyProperties(source, t);
            return t;
        }
        BeanUtils.copyProperties(source, t, ignores);
        return t;
    }

    /**
     * map转bean
     *
     * @param map  map
     * @param bean bean对象
     * @param <T>  T
     * @return T
     */
    public static <T> T toBean(Map<String, ?> map, Class<T> bean) {
        try {
            T object = bean.newInstance();
            BeanInfo beaninfo = Introspector.getBeanInfo(bean, Object.class);
            PropertyDescriptor[] pro = beaninfo.getPropertyDescriptors();
            for (PropertyDescriptor property : pro) {
                String name = property.getName();
                Object value = map.get(name);
                Method set = property.getWriteMethod();
                set.invoke(object, value);
            }
            return object;
        } catch (Exception e) {
            throw new CastException(e.getMessage());
        }
    }

    /**
     * bean转map
     *
     * @param bean bean对象
     * @return map
     */
    public static Map<String, Object> toMap(Object bean) {
        Map<String, Object> map = new HashMap<>();
        try {
            BeanInfo beaninfo = Introspector.getBeanInfo(bean.getClass(), Object.class);
            PropertyDescriptor[] pro = beaninfo.getPropertyDescriptors();
            for (PropertyDescriptor property : pro) {
                String key = property.getName();
                Method get = property.getReadMethod();
                Object value = get.invoke(bean);
                map.put(key, value);
            }
            return map;
        } catch (Exception e) {
            throw new CastException(e.getMessage());
        }
    }

    /**
     * 查找给定类中的方法
     *
     * @param clazz      目标类
     * @param methodName 方法名
     * @param paramTypes 方法参数类型
     * @return Method
     */
    public static Method findMethod(Class<?> clazz, String methodName, Class<?>... paramTypes) {
        try {
            return clazz.getMethod(methodName, paramTypes);
        } catch (NoSuchMethodException var4) {
            return findDeclaredMethod(clazz, methodName, paramTypes);
        }
    }

    /**
     * 查找给定类中声明的方法
     *
     * @param clazz      目标类
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
     *
     * @param o     字段所在的对象
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
     *
     * @param o     字段所在的对象
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
