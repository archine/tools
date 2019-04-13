package cn.gjing;

import com.google.common.collect.Maps;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author Gjing
 **/
@ControllerAdvice
@ResponseBody
public class AlitxExceptionHandler {

    @ExceptionHandler(SmsException.class)
    public ResponseEntity smsException(SmsException e) {
        e.printStackTrace();
        return ResponseEntity.badRequest().body(Maps.immutableEntry("msg", e.getMessage()));

    }

    @ExceptionHandler(OssException.class)
    public ResponseEntity ossException(OssException e) {
        e.printStackTrace();
        return ResponseEntity.badRequest().body(Maps.immutableEntry("msg", e.getMessage()));

    }
}
