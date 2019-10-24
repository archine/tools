package cn.gjing.tools.common.result;

import cn.gjing.tools.common.enums.HttpStatus;
import lombok.Data;

import java.io.Serializable;

/**
 * 返回模板
 * @author Gjing
 **/
@Data
public class ResultVO<T> implements Serializable {

    private Integer code;
    private String message;
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
        resultVO.setCode(HttpStatus.OK.getCode());
        resultVO.setMessage(HttpStatus.OK.getMsg());
        return resultVO;
    }

    public static ResultVO success() {
        ResultVO resultVO = new ResultVO();
        resultVO.setCode(HttpStatus.OK.getCode());
        resultVO.setMessage(HttpStatus.OK.getMsg());
        return resultVO;
    }

    public static ResultVO error(int code, String message) {
        ResultVO resultVO = new ResultVO();
        resultVO.setCode(code);
        resultVO.setMessage(message);
        return resultVO;
    }

    public static ResultVO error() {
        ResultVO resultVO = new ResultVO();
        resultVO.setCode(HttpStatus.BAD_REQUEST.getCode());
        resultVO.setMessage(HttpStatus.BAD_REQUEST.getMsg());
        return resultVO;
    }

    public static ResultVO error(String message) {
        ResultVO resultVO = new ResultVO();
        resultVO.setCode(HttpStatus.BAD_REQUEST.getCode());
        resultVO.setMessage(message);
        return resultVO;
    }

}
