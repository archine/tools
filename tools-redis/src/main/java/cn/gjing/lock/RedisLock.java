package cn.gjing.lock;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Gjing
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RedisLock {

    /**
     * key
     */
    String key();

    /**
     * 获取锁失败后执行的回退策略
     */
    BackType BACK_TYPE() default BackType.BLOCK;

    /**
     * 获取锁失败重试次数
     */
    int retry() default 3;

    /**
     * 过期时间,单位(s)
     */
    int expire() default 5;

    /**
     * 重试间隔时间,单位毫秒(ms)
     */
    int sleepTime() default 50;
}
