package cn.gjing.tools.redis.lock.core;

import cn.gjing.tools.redis.lock.AbstractLockTimeoutHandler;
import cn.gjing.tools.redis.lock.TimeoutException;
import org.springframework.stereotype.Component;

/**
 * @author Gjing
 **/
@Component
class ToolsLockTimeoutHandler extends AbstractLockTimeoutHandler {

    @Override
    public void getLockTimeoutFallback(String key, int expire, int timeout, int retry) {
        throw new TimeoutException("Try to get a lock timeout, with key: " + key);
    }
}
