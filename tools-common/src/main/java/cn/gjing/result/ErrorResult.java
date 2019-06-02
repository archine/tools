package cn.gjing.result;

import cn.gjing.enums.HttpStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 错误时返回
 *
 * @author Gjing
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResult implements Serializable {

    private Integer code;
    private String message;

    /**
     * 错误请求时使用,包含code和message
     *
     * @return ErrorResult
     */
    public static ErrorResult failure(Integer code, String message) {
        return ErrorResult.builder()
                .code(code)
                .message(message)
                .build();
    }

    /**
     * 错误请求时使用,包含code和message
     *
     * @return ErrorResult
     */
    public static ErrorResult failure() {
        return ErrorResult.builder()
                .code(HttpStatus.BAD_REQUEST.getCode())
                .message(HttpStatus.BAD_REQUEST.getMsg())
                .build();
    }

    /**
     * 服务器错误使用,只包含错误信息
     *
     * @return map
     */
    public static Map<String, String> error(String message) {
        Map<String, String> map = new HashMap<>(16);
        map.put("message", message == null ? HttpStatus.INTERNAL_SERVER_ERROR.getMsg() : message);
        return map;
    }

}
