package cn.gjing.tools.common.result;

import cn.gjing.tools.common.enums.HttpStatus;
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
@SuppressWarnings("unused")
public class ResultVO<T> implements Serializable {

    private int code;
    private String message;
    private T data;

    public static<T> ResultVO success(int code, String message, T data) {
        return ResultVO.builder()
                .code(code)
                .message(message)
                .data(data)
                .build();
    }

    public static <T> ResultVO success(T data) {
        return ResultVO.builder()
                .code(HttpStatus.OK.getCode())
                .message(HttpStatus.OK.getMsg())
                .data(data)
                .build();
    }

    public static ResultVO success() {
        return ResultVO.builder()
                .code(HttpStatus.OK.getCode())
                .message(HttpStatus.OK.getMsg())
                .build();
    }

    public static ResultVO error(int code, String message) {
        return ResultVO.builder()
                .code(code)
                .message(message)
                .build();
    }

    public static ResultVO error() {
        return ResultVO.builder()
                .code(HttpStatus.BAD_REQUEST.getCode())
                .message(HttpStatus.BAD_REQUEST.getMsg())
                .build();
    }

    public static ResultVO error(String message) {
        return ResultVO.builder()
                .code(HttpStatus.BAD_REQUEST.getCode())
                .message(message)
                .build();
    }

}
