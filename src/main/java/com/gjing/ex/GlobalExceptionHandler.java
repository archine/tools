package com.gjing.ex;

import com.gjing.enums.HttpStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author Archine
 **/
@ControllerAdvice
@ResponseBody
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ParamException.class)
    public ResponseEntity<String> paramException(ParamException e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.PARAM_EMPTY.getCode()).body(e.getMessage());
    }

    @ExceptionHandler(HttpException.class)
    public ResponseEntity<String> httpException(HttpException e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST.getCode()).body(e.getMessage());
    }

    @ExceptionHandler(SmsException.class)
    public ResponseEntity<String> smsException(SmsException e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST.getCode()).body(e.getMessage());
    }
}
