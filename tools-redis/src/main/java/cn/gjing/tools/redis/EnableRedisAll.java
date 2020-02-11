package cn.gjing.tools.redis;

import cn.gjing.tools.redis.cache.core.EnableToolsCache;
import cn.gjing.tools.redis.lock.core.EnableToolsLock;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Gjing
 **/
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@EnableToolsLock
@EnableToolsCache
public @interface EnableRedisAll {
}
