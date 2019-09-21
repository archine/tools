package cn.gjing.tools.redis.lock;

/**
 * @author Gjing
 **/
public abstract class AbstractLockTimeoutHandler {
    /**
     * 获取超时后处理逻辑
     *
     * @param key 加锁的key
     * @param expire 锁过期时间
     * @param retry 重新尝试获取锁的间隔
     * @param timeout 超时时间
     */
    public abstract void getLockTimeoutFallback(String key, int expire, int timeout, int retry);
}
