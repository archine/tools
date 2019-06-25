package cn.gjing.lock.core;

import cn.gjing.lock.AbstractLockTimeoutHandler;
import cn.gjing.lock.TimeoutException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author Gjing
 **/
@Component
class ToolsLockTimeoutHandler extends AbstractLockTimeoutHandler {
    @Override
    @ResponseBody
    public ResponseEntity timeoutAfter(TimeoutException e) {
        return ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT.value()).body(HttpStatus.REQUEST_TIMEOUT.getReasonPhrase());
    }
}
