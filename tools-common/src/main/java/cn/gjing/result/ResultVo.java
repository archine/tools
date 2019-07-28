package cn.gjing.result;

import cn.gjing.enums.HttpStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author Gjing
 * 返回模板
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResultVo<T> implements Serializable {

    private Integer code;
    private String message;
    private T data;

    public static<T> ResultVo success(Integer code,String message,T data) {
        return ResultVo.builder()
                .code(code == null ? HttpStatus.OK.getCode() : code)
                .message(message == null ? HttpStatus.OK.getMsg() : message)
                .data(data)
                .build();
    }

    public static <T> ResultVo success(T data) {
        return ResultVo.builder()
                .code(HttpStatus.OK.getCode())
                .message(HttpStatus.OK.getMsg())
                .data(data)
                .build();
    }

    public static ResultVo success() {
        return ResultVo.builder()
                .code(HttpStatus.OK.getCode())
                .message(HttpStatus.OK.getMsg())
                .build();
    }

    public static ResultVo error(Integer code,String message) {
        return ResultVo.builder()
                .code(code == null ? HttpStatus.BAD_REQUEST.getCode() : code)
                .message(message == null ? HttpStatus.BAD_REQUEST.getMsg() : message)
                .build();
    }

    public static ResultVo error() {
        return ResultVo.builder()
                .code(HttpStatus.BAD_REQUEST.getCode())
                .message(HttpStatus.BAD_REQUEST.getMsg())
                .build();
    }
}
