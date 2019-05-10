package cn.gjing.result;

import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.util.Map;

/**
 * 错误时返回
 * @author Gjing
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResult {

    private Integer code;
    private String message;

    /**
     * 错误请求时使用,包含code和message
     * @return ErrorResult
     */
    public static ErrorResult failure() {
        return ErrorResult.builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .message(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .build();
    }

    /**
     * 错误请求时使用,包含code和message
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
     * @return ErrorResult
     */
    public static ErrorResult failure(String message) {
        return ErrorResult.builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .message(message)
                .build();
    }

    /**
     * 服务器错误使用,只包含错误信息
     * @param message 错误信息
     * @return map
     */
    public static Map.Entry<String, String> error(String message) {
        return Maps.immutableEntry("message", message);
    }

    /**
     * 服务器错误使用,只包含错误信息
     * @return map
     */
    public static Map.Entry<String, String> error() {
        return Maps.immutableEntry("message", HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
    }

}
