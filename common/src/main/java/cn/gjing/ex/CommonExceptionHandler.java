package cn.gjing.ex;

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
public class CommonExceptionHandler {

    @ExceptionHandler(ParamException.class)
    public ResponseEntity paramException(ParamException e) {
        e.printStackTrace();
        return ResponseEntity.badRequest().body(Maps.immutableEntry("msg", e.getMessage()));
    }

    @ExceptionHandler(HttpException.class)
    public ResponseEntity httpException(HttpException e) {
        e.printStackTrace();
        return ResponseEntity.badRequest().body(Maps.immutableEntry("msg", e.getMessage()));
    }

    @ExceptionHandler(GjingException.class)
    public ResponseEntity gjingException(GjingException e) {
        e.printStackTrace();
        return ResponseEntity.badRequest().body(Maps.immutableEntry("msg", e.getMessage()));
    }
    
}
