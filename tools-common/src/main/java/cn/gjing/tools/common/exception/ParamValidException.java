package cn.gjing.tools.common.exception;

/**
 * 参数异常
 * @author Gjing
 **/
public class ParamValidException extends RuntimeException {
    public ParamValidException(String message) {
        super(message);
    }
}
