package cn.gjing.tools.redis.lock.core;

import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Gjing
 * 开启redis分布式锁
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import(ToolsRedisLockSelector.class)
public @interface EnableToolsLock {
}
