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
public class SwaggerExceptionHandler {
    @ExceptionHandler(RegisterBeanException.class)
    public ResponseEntity registerBean(RegisterBeanException e) {
        return ResponseEntity.badRequest().body(Maps.immutableEntry("msg",e.getMessage()));
    }
}
