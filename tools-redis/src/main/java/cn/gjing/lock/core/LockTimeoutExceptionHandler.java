package cn.gjing.lock.core;

import cn.gjing.lock.TimeoutException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author Gjing
 **/
@RestControllerAdvice
class LockTimeoutExceptionHandler {
    @ExceptionHandler(TimeoutException.class)
    public ResponseEntity timeoutException(TimeoutException e){
        return ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT.value()).body(HttpStatus.REQUEST_TIMEOUT.getReasonPhrase());
    }
}
