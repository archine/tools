package cn.gjing.lock.core;

import cn.gjing.lock.AbstractLockTimeoutHandler;
import cn.gjing.lock.TimeoutException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

/**
 * @author Gjing
 **/
@Component
class ToolsLockTimeoutHandler extends AbstractLockTimeoutHandler<ResponseEntity> {
    @Override
    public ResponseEntity timeoutAfter(TimeoutException e) {
        return ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT.value()).body(HttpStatus.REQUEST_TIMEOUT.getReasonPhrase());
    }
}
