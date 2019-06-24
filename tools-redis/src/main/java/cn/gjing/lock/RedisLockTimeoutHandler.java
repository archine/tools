package cn.gjing.lock;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * @author Gjing
 **/
class RedisLockTimeoutHandler extends AbstractRedisLockTimeoutHandle {
    @Override
    public ResponseEntity timeoutException(TimeoutException e) {
        return ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).body(e.getMessage());
    }
}
