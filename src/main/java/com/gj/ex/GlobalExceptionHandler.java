package com.gj.ex;

import com.gj.enums.HttpStatus;
import com.gj.utils.resp.ResultVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author GJ
 * @date 2019-03-12
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
}
