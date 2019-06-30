package cn.gjing.lock;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author Gjing
 **/
@RestControllerAdvice
public abstract class AbstractLockTimeoutHandler<T> {
    /**
     * 超时后处理逻辑
     */
    @ExceptionHandler(TimeoutException.class)
    public abstract T timeoutAfter(TimeoutException e);
}
