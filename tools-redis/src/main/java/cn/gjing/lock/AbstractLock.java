package cn.gjing.lock;

import org.springframework.data.redis.core.script.DefaultRedisScript;

/**
 * @author Gjing
 **/
public interface AbstractLock {
    /**
     * 加锁
     * 锁成功返回key, 否则返回null
     */
    String lock(DefaultRedisScript<String> redisScript, String key, String val, int expire);

    /**
     * 释放锁
     * 释放成功返回key,否则返回null
     */
    String release(DefaultRedisScript<String> redisScript, String key);

}
