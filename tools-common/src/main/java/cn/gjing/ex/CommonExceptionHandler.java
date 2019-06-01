package cn.gjing.ex;

import cn.gjing.enums.HttpStatus;
import cn.gjing.result.ErrorResult;
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
        return ResponseEntity.badRequest().body(ErrorResult.error( e.getMessage()));
    }

    @ExceptionHandler(HttpException.class)
    public ResponseEntity httpException(HttpException e) {
        e.printStackTrace();
        return ResponseEntity.badRequest().body(ErrorResult.error(e.getMessage()));
    }

    @ExceptionHandler(AuthException.class)
    public ResponseEntity noAuth(AuthException e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED.getCode()).body(ErrorResult.error(e.getMessage()));
    }
    
}
