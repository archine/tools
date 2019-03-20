package com.gjing.ex;

import com.gjing.enums.HttpStatus;
import com.gjing.utils.result.ResultVo;
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
    public ResultVo paramException(ParamException e) {
        e.printStackTrace();
        return ResultVo.error(HttpStatus.PARAM_EMPTY.getCode(), e.getMessage());
    }

    @ExceptionHandler(HttpException.class)
    public ResponseEntity<String> httpException(HttpException e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST.getCode()).body(e.getMessage());
    }
}
