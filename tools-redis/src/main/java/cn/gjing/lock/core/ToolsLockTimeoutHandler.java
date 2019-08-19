package cn.gjing.lock.core;

import cn.gjing.lock.AbstractLockTimeoutHandler;
import cn.gjing.lock.TimeoutException;
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
