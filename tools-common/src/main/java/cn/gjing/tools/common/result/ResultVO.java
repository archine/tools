package cn.gjing.tools.common.result;

import lombok.Data;
import org.springframework.http.HttpStatus;

import java.io.Serializable;

/**
 * 返回模板
 * @author Gjing
 **/
@Data
public class ResultVO<T> implements Serializable {
    /**
     * 状态码
     */
    private Integer code;
    /**
     * 提示信息
     */
    private String message;
    /**
     * 数据
     */
    private T data;

    private ResultVO() {

    }

    public static<T> ResultVO<T> success(int code, String message, T data) {
        ResultVO<T> resultVO = new ResultVO<>();
        resultVO.setData(data);
        resultVO.setCode(code);
        resultVO.setMessage(message);
        return resultVO;
    }

    public static <T> ResultVO<T> success(T data) {
        ResultVO<T> resultVO = new ResultVO<>();
        resultVO.setData(data);
        resultVO.setCode(HttpStatus.OK.value());
        resultVO.setMessage(HttpStatus.OK.getReasonPhrase());
        return resultVO;
    }

    public static ResultVO<String> success() {
        ResultVO<String> resultVO = new ResultVO<>();
        resultVO.setCode(HttpStatus.OK.value());
        resultVO.setMessage(HttpStatus.OK.getReasonPhrase());
        return resultVO;
    }

    public static ResultVO<String> error(int code, String message) {
        ResultVO<String> resultVO = new ResultVO<>();
        resultVO.setCode(code);
        resultVO.setMessage(message);
        return resultVO;
    }

    public static ResultVO<String> error() {
        ResultVO<String> resultVO = new ResultVO<>();
        resultVO.setCode(HttpStatus.BAD_REQUEST.value());
        resultVO.setMessage(HttpStatus.BAD_REQUEST.getReasonPhrase());
        return resultVO;
    }

    public static ResultVO<String> error(String message) {
        ResultVO<String> resultVO = new ResultVO<>();
        resultVO.setCode(HttpStatus.BAD_REQUEST.value());
        resultVO.setMessage(message);
        return resultVO;
    }

}
