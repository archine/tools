package cn.gjing.lock.core;

import cn.gjing.lock.AbstractLockTimeoutHandler;
import cn.gjing.lock.TimeoutException;

/**
 * @author Gjing
 **/
public class ToolsLockTimeoutHandler extends AbstractLockTimeoutHandler {
    @Override
    public void timeoutAfter() {
        throw new TimeoutException();
    }
}
