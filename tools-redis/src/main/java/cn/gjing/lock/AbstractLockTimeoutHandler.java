package cn.gjing.lock;

/**
 * @author Gjing
 **/
public abstract class AbstractLockTimeoutHandler {
    /**
     * 超时后处理逻辑
     */
    public abstract void timeoutAfter();
}
