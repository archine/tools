package cn.gjing.util;

import cn.gjing.exception.CastException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Gjing
 **/
@Component
public class BeanUtil implements ApplicationContextAware {
    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if (BeanUtil.applicationContext == null) {
            BeanUtil.applicationContext = applicationContext;
        }
    }

    /**
     * 获取ApplicationContext实例
     *
     * @return ApplicationContext
     */
    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    /**
     * 获取bean
     *
     * @param beanName bean名称
     * @return bean
     */
    public static Object getBean(String beanName) {
        return applicationContext.getBean(beanName);
    }

    /**
     * 获取一个bean
     *
     * @param beanClass bean的类对象
     * @param <T>       T
     * @return T
     */
    public static <T> T getBean(Class<T> beanClass) {
        return applicationContext.getBean(beanClass);
    }


    /**
     * 复制属性
     *
     * @param source  原对象
     * @param target  目标对象
     */
    public static void copyProperties(Object source, Object target) {
        BeanUtils.copyProperties(source, target);
    }
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
     * @param <T>     T
     * @return T
     */
    public static <T> T copyProperties(Object source, Class<T> target) {
        try {
            T t = target.newInstance();
            BeanUtil.copyProperties(source, t);
            return t;
        } catch (Exception e) {
            throw new CastException(e.getMessage());
        }
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
     * @param map map
     * @param bean bean对象
     * @param <T> T
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
     * @param bean bean对象
     * @return map
     */
    public static Map<String,Object> toMap(Object bean){
        Map<String, Object> map=new HashMap<>();
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
}
