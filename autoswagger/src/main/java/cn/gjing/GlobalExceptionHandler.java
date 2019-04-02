package cn.gjing;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author Gjing
 **/
@ControllerAdvice
@ResponseBody
public class GlobalExceptionHandler {
    @ExceptionHandler(RegisterBeanException.class)
    public ResponseEntity registerBean(RegisterBeanException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
