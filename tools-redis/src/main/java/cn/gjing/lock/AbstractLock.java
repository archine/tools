package cn.gjing.lock;

/**
 * @author Gjing
 **/
public abstract class AbstractLock {
    /**
     * 加锁
     *
     * @param expire      超时时间
     * @param val         value
     * @param key         key
     * @param retry 从新获取锁得间隔
     * @param timeout 超时时间
     * @return 锁成功返回val, 否则返回null
     */
    public abstract String lock(String key, String val, int expire,int timeout,int retry);

    /**
     * 释放锁
     *
     * @param key         key
     * @param val         value
     * @return 释放成功返回释放的锁对应的key值, 否则返回null
     */
    public abstract String release(String key, String val);
}
