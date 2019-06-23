package cn.gjing.lock;

/**
 * @author Gjing
 **/
public enum BackType {
    /**
     * 获取锁失败后执行的策略, 重试和回退机制
     */
    RETRY, BLOCK;
}
