package cn.gjing.lock;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author Gjing
 **/
@RestControllerAdvice
public abstract class AbstractRedisLockTimeoutHandle {

    @ExceptionHandler(TimeoutException.class)
    public abstract ResponseEntity timeoutException(TimeoutException e);
}
